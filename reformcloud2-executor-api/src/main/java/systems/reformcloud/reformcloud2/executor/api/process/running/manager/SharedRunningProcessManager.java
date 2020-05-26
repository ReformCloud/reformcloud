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
package systems.reformcloud.reformcloud2.executor.api.process.running.manager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.process.running.RunningProcess;
import systems.reformcloud.reformcloud2.executor.api.process.running.events.RunningProcessWatchdogStoppedEvent;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.utility.optional.ReferencedOptional;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class SharedRunningProcessManager {

    private static final Collection<RunningProcess> ALL_PROCESSES = new CopyOnWriteArrayList<>();

    static {
        tick();
    }

    private SharedRunningProcessManager() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static ReferencedOptional<RunningProcess> getProcessByName(@NotNull String name) {
        return Streams.filterToReference(
                ALL_PROCESSES,
                e -> e.getProcessInformation().getProcessDetail().getName().equals(name)
        );
    }

    @NotNull
    public static ReferencedOptional<RunningProcess> getProcessByUniqueId(@NotNull UUID uniqueID) {
        return Streams.filterToReference(
                ALL_PROCESSES,
                e -> e.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(uniqueID)
        );
    }

    public static void registerRunningProcess(@NotNull RunningProcess process) {
        ALL_PROCESSES.add(process);
    }

    public static void unregisterProcess(@NotNull UUID uniqueId) {
        getProcessByUniqueId(uniqueId).ifPresent(ALL_PROCESSES::remove);
    }

    public static void shutdownAll() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        ALL_PROCESSES.stream()
                .filter(e -> e.getProcess().isPresent())
                .forEach(e -> executorService.submit(() -> e.shutdown()));

        try {
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (final InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @NotNull
    @UnmodifiableView
    public static Collection<RunningProcess> getAllProcesses() {
        return Collections.unmodifiableCollection(ALL_PROCESSES);
    }

    private static void tick() {
        if (CommonHelper.SCHEDULED_EXECUTOR_SERVICE.isShutdown()) {
            return;
        }

        CommonHelper.SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            for (RunningProcess process : ALL_PROCESSES) {
                if (!process.isAlive()
                        && process.getProcess().isPresent()
                        && process.getStartupTime() != -1
                        && process.getStartupTime() + TimeUnit.SECONDS.toMillis(30) < System.currentTimeMillis()) {
                    process.shutdown();
                    ExecutorAPI.getInstance().getEventManager().callEvent(new RunningProcessWatchdogStoppedEvent(process));
                    continue;
                }

                process.getProcessScreen().callUpdate();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}
