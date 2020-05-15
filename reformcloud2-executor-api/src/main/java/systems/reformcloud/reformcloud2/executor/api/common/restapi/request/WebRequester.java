/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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
package systems.reformcloud.reformcloud2.executor.api.common.restapi.request;

import io.netty.channel.Channel;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionResult;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

public interface WebRequester extends Nameable {

    /**
     * @return The http channel of the web requester
     */
    @NotNull
    Channel channel();

    /**
     * @return If the requester is still connected
     */
    boolean isConnected();

    /**
     * Checks if the specified permission is set
     *
     * @param perm The permission which should get checked
     * @return If the permission is set
     */
    @NotNull
    PermissionResult hasPermissionValue(@NotNull String perm);

    /**
     * Checks if the specified permission is set
     *
     * @param perm The permission which should get checked
     * @return If the permission is set
     * @see #hasPermissionValue(String)
     */
    default boolean hasPermission(@NotNull String perm) {
        return hasPermissionValue(perm).isAllowed();
    }
}
