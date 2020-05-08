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
package systems.reformcloud.reformcloud2.executor.client.process.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.client.process.ProcessManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.manager.SharedRunningProcessManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;
import systems.reformcloud.reformcloud2.executor.controller.network.packet.ClientPacketProcessRegistered;

import java.util.Collection;
import java.util.UUID;

public final class DefaultProcessManager implements ProcessManager {

    @Override
    public void registerProcess(@NotNull RunningProcess runningProcess) {
        SharedRunningProcessManager.registerRunningProcess(runningProcess);

        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new ClientPacketProcessRegistered(
                runningProcess.getProcessInformation().getProcessDetail().getProcessUniqueID(),
                runningProcess.getProcessInformation().getProcessDetail().getName()
        )));
    }

    @Override
    public void unregisterProcess(@NotNull String name) {
        SharedRunningProcessManager.getProcessByName(name)
                .ifPresent(e -> SharedRunningProcessManager.unregisterProcess(e.getProcessInformation().getProcessDetail().getProcessUniqueID()));
    }

    @NotNull
    @Override
    public ReferencedOptional<RunningProcess> getProcess(@NotNull UUID uniqueID) {
        return SharedRunningProcessManager.getProcessByUniqueId(uniqueID);
    }

    @NotNull
    @Override
    public ReferencedOptional<RunningProcess> getProcess(String name) {
        return SharedRunningProcessManager.getProcessByName(name);
    }

    @NotNull
    @Override
    public Collection<RunningProcess> getAll() {
        return SharedRunningProcessManager.getAllProcesses();
    }

    @Override
    public void onProcessDisconnect(@NotNull UUID uuid) {
        this.getProcess(uuid).ifPresent(RunningProcess::shutdown);
    }

    @Override
    public void stopAll() {
        SharedRunningProcessManager.shutdownAll();
    }
}
