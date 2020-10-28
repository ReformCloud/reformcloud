/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.signs.util;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;

import java.util.function.Function;

public final class Utils {

    private Utils() {
        throw new UnsupportedOperationException();
    }

    public static boolean canConnect(@NotNull ProcessInformation target, @NotNull Function<String, Boolean> permissionChecker) {
        if (!target.getNetworkInfo().isConnected()) {
            return false;
        }

        boolean canConnect = true;
        if (target.getProcessGroup().getPlayerAccessConfiguration().isMaintenance()) {
            canConnect = permissionChecker.apply(target.getProcessGroup().getPlayerAccessConfiguration().getMaintenanceJoinPermission());
        }

        if (canConnect && target.getProcessGroup().getPlayerAccessConfiguration().isUseCloudPlayerLimit()
            && target.getProcessPlayerManager().getOnlineCount() >= target.getProcessDetail().getMaxPlayers()) {
            canConnect = permissionChecker.apply(target.getProcessGroup().getPlayerAccessConfiguration().getFullJoinPermission());
        }

        if (canConnect && target.getProcessGroup().getPlayerAccessConfiguration().isJoinOnlyPerPermission()) {
            canConnect = permissionChecker.apply(target.getProcessGroup().getPlayerAccessConfiguration().getJoinPermission());
        }

        ProcessState state = target.getProcessDetail().getProcessState();
        if (canConnect && (!state.isOnline() || state.equals(ProcessState.INVISIBLE))) {
            canConnect = permissionChecker.apply(target.getProcessGroup().getPlayerAccessConfiguration().getFullJoinPermission());
        }

        return canConnect;
    }

    public static boolean canConnectPerState(@NotNull ProcessInformation processInformation) {
        ProcessState state = processInformation.getProcessDetail().getProcessState();
        return state.equals(ProcessState.STARTED) || state.equals(ProcessState.FULL) || state.equals(ProcessState.READY);
    }
}
