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
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;
import systems.reformcloud.reformcloud2.executor.client.ClientExecutor;
import systems.reformcloud.reformcloud2.executor.client.screen.ProcessScreen;
import systems.reformcloud.reformcloud2.executor.controller.network.packet.ClientPacketProcessRegistered;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class DefaultProcessManager implements ProcessManager {

    private final List<RunningProcess> list = new CopyOnWriteArrayList<>();

    @Override
    public void registerProcess(@NotNull RunningProcess runningProcess) {
        list.add(runningProcess);
        ClientExecutor.getInstance().getScreenManager().getPerProcessScreenLines().put(
                runningProcess.getProcessInformation().getProcessDetail().getProcessUniqueID(),
                new ProcessScreen(runningProcess.getProcessInformation().getProcessDetail().getProcessUniqueID())
        );

        DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new ClientPacketProcessRegistered(
                runningProcess.getProcessInformation().getProcessDetail().getProcessUniqueID(),
                runningProcess.getProcessInformation().getProcessDetail().getName()
        )));
    }

    @Override
    public void unregisterProcess(@NotNull String name) {
        Streams.filterToReference(list, runningProcess -> runningProcess.getProcessInformation().getProcessDetail().getName().equals(name)).ifPresent(runningProcess -> {
            list.remove(runningProcess);
            ClientExecutor.getInstance().getScreenManager().getPerProcessScreenLines().remove(runningProcess.getProcessInformation().getProcessDetail().getProcessUniqueID());
        });
    }

    @NotNull
    @Override
    public ReferencedOptional<RunningProcess> getProcess(@NotNull UUID uniqueID) {
        return Streams.filterToReference(list, runningProcess -> runningProcess.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(uniqueID));
    }

    @NotNull
    @Override
    public ReferencedOptional<RunningProcess> getProcess(String name) {
        return Streams.filterToReference(list, runningProcess -> runningProcess.getProcessInformation().getProcessDetail().getName().equals(name));
    }

    @NotNull
    @Override
    public Collection<RunningProcess> getAll() {
        return Collections.unmodifiableCollection(list);
    }

    @Override
    public void onProcessDisconnect(@NotNull UUID uuid) {
        Streams.filterToReference(list, runningProcess -> runningProcess.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(uuid)).ifPresent(RunningProcess::shutdown);
    }

    @Override
    public void stopAll() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        this.list.stream().filter(e -> e.getProcess().isPresent()).forEach(e -> executorService.submit(() -> e.shutdown()));

        try {
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (final InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
