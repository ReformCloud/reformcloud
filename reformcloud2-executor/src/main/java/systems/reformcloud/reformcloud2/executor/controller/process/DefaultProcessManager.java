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
package systems.reformcloud.reformcloud2.executor.controller.process;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStartedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStoppedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.shared.EventPacketProcessClosed;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.shared.EventPacketProcessStarted;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.shared.EventPacketProcessUpdated;
import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.RuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.basic.FileTemplateBackend;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.NetworkInfo;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.detail.ProcessDetail;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.matcher.PreparedProcessFilter;
import systems.reformcloud.reformcloud2.executor.api.common.process.util.MemoryCalculator;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Duo;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.api.controller.process.ProcessManager;
import systems.reformcloud.reformcloud2.executor.client.network.packet.ControllerPacketProcessDisconnected;
import systems.reformcloud.reformcloud2.executor.client.network.packet.ControllerPacketStartPreparedProcess;
import systems.reformcloud.reformcloud2.executor.client.network.packet.ControllerPacketStartProcess;
import systems.reformcloud.reformcloud2.executor.client.network.packet.ControllerPacketStopProcess;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;
import systems.reformcloud.reformcloud2.executor.node.util.ProcessCopyOnWriteArrayList;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;

public final class DefaultProcessManager implements ProcessManager {

    private final Collection<ProcessInformation> processInformation = Collections.synchronizedCollection(new ProcessCopyOnWriteArrayList());

    private final Queue<Duo<ProcessConfiguration, Boolean>> noClientTryLater = new ConcurrentLinkedQueue<>();

    public DefaultProcessManager() {
        CompletableFuture.runAsync(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (!noClientTryLater.isEmpty()) {
                    Duo<ProcessConfiguration, Boolean> duo = noClientTryLater.peek();
                    if (duo.getSecond()) {
                        startProcess(duo.getFirst());
                    } else {
                        prepareProcess(duo.getFirst());
                    }

                    noClientTryLater.remove(duo);
                }

                AbsoluteThread.sleep(TimeUnit.MILLISECONDS, 200);
            }
        });
    }

    @Override
    public List<ProcessInformation> getAllProcesses() {
        return Streams.newList(processInformation);
    }

    @Override
    public List<ProcessInformation> getProcesses(String group) {
        return Streams.list(getAllProcesses(), processInformation -> processInformation.getProcessGroup().getName().equals(group));
    }

    @Override
    public Long getOnlineAndWaitingProcessCount(String group) {
        return getProcesses(group).stream().filter(e -> e.getProcessDetail().getProcessState().isValid()).count() + getWaitingProcesses(group);
    }

    @Override
    public Integer getWaitingProcesses(String group) {
        return noClientTryLater.stream().filter(e -> e.getFirst().getBase().getName().equals(group)).mapToInt(value -> 1).sum();
    }

    @Override
    public ProcessInformation getProcess(String name) {
        requireNonNull(name);
        return Streams.filter(processInformation, processInformation -> processInformation.getProcessDetail().getName().equals(name));
    }

    @Override
    public ProcessInformation getProcess(UUID uniqueID) {
        requireNonNull(uniqueID);
        return Streams.filter(processInformation, processInformation -> processInformation.getProcessDetail().getProcessUniqueID().equals(uniqueID));
    }

    @Override
    public synchronized ProcessInformation startProcess(@NotNull ProcessConfiguration configuration) {
        ProcessInformation matching = PreparedProcessFilter.findMayMatchingProcess(
                configuration, this.getPreparedProcesses(configuration.getBase().getName())
        );
        if (matching != null) {
            System.out.println(LanguageManager.get(
                    "process-start-already-prepared-process",
                    configuration.getBase().getName(),
                    matching.getProcessDetail().getName()
            ));
            this.startProcess(matching);
            return matching;
        }

        ProcessInformation processInformation = this.create(configuration, true);
        if (processInformation == null) {
            return null;
        }

        this.processInformation.add(processInformation);
        DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getParentName()).ifPresent(packetSender -> packetSender.sendPacket(new ControllerPacketStartProcess(processInformation, true)));
        //Send event packet to notify processes
        DefaultChannelManager.INSTANCE.getAllSender().forEach(packetSender -> packetSender.sendPacket(new EventPacketProcessStarted(processInformation)));
        ControllerExecutor.getInstance().getEventManager().callEvent(new ProcessStartedEvent(processInformation));
        return processInformation;
    }

    @NotNull
    @Override
    public synchronized ProcessInformation startProcess(@NotNull ProcessInformation processInformation) {
        if (processInformation.getProcessDetail().getProcessState().equals(ProcessState.PREPARED)) {
            DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getParentName()).ifPresent(
                    e -> e.sendPacket(new ControllerPacketStartPreparedProcess(processInformation))
            );
        }

        return processInformation;
    }

    @Override
    public synchronized ProcessInformation prepareProcess(@NotNull ProcessConfiguration configuration) {
        ProcessInformation processInformation = this.create(configuration, false);
        if (processInformation == null) {
            return null;
        }

        this.processInformation.add(processInformation);
        DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getParentName()).ifPresent(packetSender -> packetSender.sendPacket(new ControllerPacketStartProcess(processInformation, false)));
        return processInformation;
    }

    @Override
    public ProcessInformation stopProcess(String name) {
        ProcessInformation processInformation = getProcess(name);
        if (processInformation == null) {
            return null;
        }

        return stopProcess(processInformation.getProcessDetail().getProcessUniqueID());
    }

    @Override
    public ProcessInformation stopProcess(UUID uniqueID) {
        ProcessInformation processInformation = getProcess(uniqueID);
        if (processInformation == null) {
            return null;
        }

        DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getParentName()).ifPresent(packetSender -> packetSender.sendPacket(new ControllerPacketStopProcess(processInformation.getProcessDetail().getProcessUniqueID())));
        return processInformation;
    }

    @Override
    public void onClientDisconnect(String clientName) {
        Streams.allOf(processInformation, processInformation -> processInformation.getProcessDetail().getParentName().equals(clientName)).forEach(processInformation -> {
            DefaultProcessManager.this.processInformation.remove(processInformation);
            notifyDisconnect(processInformation);
        });
    }

    private ProcessInformation create(ProcessConfiguration configuration, boolean start) {
        Template template = configuration.getTemplate();
        if (template == null) {
            AtomicReference<Template> bestTemplate = new AtomicReference<>();
            configuration.getBase().getTemplates().forEach(template1 -> {
                if (bestTemplate.get() == null) {
                    bestTemplate.set(template1);
                } else {
                    if (bestTemplate.get().getPriority() < template1.getPriority()) {
                        bestTemplate.set(template1);
                    }
                }
            });

            if (bestTemplate.get() == null) {
                bestTemplate.set(new Template(0, "default", false, FileTemplateBackend.NAME, "#", new RuntimeConfiguration(
                        512, new ArrayList<>(), new HashMap<>()
                ), Version.PAPER_1_8_8));

                System.err.println("Starting up process " + configuration.getBase().getName() + " with default template because no template is set up");
                Thread.dumpStack();
            }

            template = bestTemplate.get();
        }

        ClientRuntimeInformation client = this.client(
                configuration.getBase(),
                configuration.getMaxMemory() == null ? template.getRuntimeConfiguration().getMaxMemory() : configuration.getMaxMemory()
        );
        if (client == null) {
            noClientTryLater.add(new Duo<>(configuration, start));
            return null;
        }

        int id = configuration.getId() == -1 ? this.nextID(configuration.getBase()) : configuration.getId();
        int port = configuration.getPort() == null ? nextPort(configuration.getBase()) : configuration.getPort();
        UUID uniqueID = configuration.getUniqueId();

        String displayName = configuration.getDisplayName();
        if (displayName == null) {
            StringBuilder stringBuilder = new StringBuilder().append(configuration.getBase().getName());
            if (configuration.getBase().isShowIdInName()) {
                if (template.getServerNameSplitter() != null) {
                    stringBuilder.append(template.getServerNameSplitter());
                }

                stringBuilder.append(id);
            }

            displayName = stringBuilder.toString();
        }

        for (ProcessInformation allProcess : this.getAllProcesses()) {
            if (allProcess.getProcessDetail().getId() == id) {
                id = nextID(configuration.getBase());
            }

            if (allProcess.getNetworkInfo().getPort() == port) {
                port = nextPort(configuration.getBase());
            }

            if (allProcess.getProcessDetail().getProcessUniqueID().equals(uniqueID)) {
                uniqueID = UUID.randomUUID();
            }

            if (allProcess.getProcessDetail().getDisplayName().equals(displayName)) {
                displayName += UUID.randomUUID().toString().split("-")[0];
            }
        }

        ProcessInformation processInformation = new ProcessInformation(
                new ProcessDetail(
                        uniqueID,
                        client.uniqueID(),
                        client.getName(),
                        configuration.getBase().getName() + template.getServerNameSplitter() + id,
                        displayName,
                        id,
                        template,
                        configuration.getMaxMemory() == null
                                ? MemoryCalculator.calcMemory(configuration.getBase().getName(), template)
                                : configuration.getMaxMemory(),
                        configuration.getInitialState()
                ),
                new NetworkInfo(
                        client.startHost(),
                        port
                ), configuration.getBase(), configuration.getExtra(), configuration.getInclusions()
        );
        return processInformation.updateMaxPlayers(null);
    }

    private int nextID(ProcessGroup processGroup) {
        int id = 1;
        Collection<Integer> ids = Streams.newCollection(processInformation, processInformation -> processInformation.getProcessGroup().getName().equals(processGroup.getName()), e -> e.getProcessDetail().getId());

        while (ids.contains(id)) {
            id++;
        }

        return id;
    }

    private int nextPort(ProcessGroup processGroup) {
        int port = processGroup.getStartupConfiguration().getStartPort();
        Collection<Integer> ports = Streams.newCollection(processInformation, processInformation -> processInformation.getNetworkInfo().getPort());

        while (ports.contains(port)) {
            port++;
        }

        return port;
    }

    private ClientRuntimeInformation client(ProcessGroup processGroup, int maxMemory) {
        if (processGroup.getStartupConfiguration().isSearchBestClientAlone()) {
            AtomicReference<ClientRuntimeInformation> best = new AtomicReference<>();
            Streams.newCollection(ClientManager.INSTANCE.getClientRuntimeInformation(), clientRuntimeInformation -> {
                Collection<Integer> startedOn = Streams.newCollection(processInformation,
                        processInformation -> processInformation.getProcessDetail().getParentName().equals(clientRuntimeInformation.getName()),
                        processInformation -> processInformation.getProcessDetail().getMaxMemory()
                );

                int usedMemory = 0;
                for (Integer integer : startedOn) {
                    usedMemory += integer;
                }

                if (startedOn.size() < clientRuntimeInformation.maxProcessCount() || clientRuntimeInformation.maxProcessCount() == -1) {
                    return clientRuntimeInformation.maxMemory() > (usedMemory + maxMemory);
                }

                return false;
            }, (UnaryOperator<ClientRuntimeInformation>) clientRuntimeInformation -> clientRuntimeInformation).forEach(clientRuntimeInformation -> {
                if (best.get() == null) {
                    best.set(clientRuntimeInformation);
                }
            });

            return best.get();
        } else {
            AtomicReference<ClientRuntimeInformation> best = new AtomicReference<>();
            Streams.newCollection(ClientManager.INSTANCE.getClientRuntimeInformation(), clientRuntimeInformation -> {
                if (!processGroup.getStartupConfiguration().getUseOnlyTheseClients().contains(clientRuntimeInformation.getName())) {
                    return false;
                }

                Collection<Integer> startedOn = Streams.newCollection(
                        processInformation,
                        processInformation -> processInformation.getProcessDetail().getParentName().equals(clientRuntimeInformation.getName()),
                        processInformation -> processInformation.getProcessDetail().getMaxMemory()
                );

                int usedMemory = 0;
                for (Integer integer : startedOn) {
                    usedMemory += integer;
                }

                if (startedOn.size() < clientRuntimeInformation.maxProcessCount() || clientRuntimeInformation.maxProcessCount() == -1) {
                    return clientRuntimeInformation.maxMemory() > (usedMemory + maxMemory);
                }

                return false;
            }, (UnaryOperator<ClientRuntimeInformation>) clientRuntimeInformation -> clientRuntimeInformation).forEach(clientRuntimeInformation -> {
                if (best.get() == null) {
                    best.set(clientRuntimeInformation);
                }
            });

            return best.get();
        }
    }

    @Override
    public Iterator<ProcessInformation> iterator() {
        return Streams.newList(processInformation).iterator();
    }

    @Override
    public void update(@NotNull ProcessInformation processInformation) {
        synchronized (processInformation) {
            ProcessInformation current = getProcess(processInformation.getProcessDetail().getProcessUniqueID());
            if (current == null) {
                return;
            }

            Streams.filterToReference(this.processInformation,
                    e -> e.getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID())).ifPresent(e -> {
                this.processInformation.remove(e);
                this.processInformation.add(processInformation);
            });
        }

        DefaultChannelManager.INSTANCE.getAllSender().forEach(packetSender -> packetSender.sendPacket(new EventPacketProcessUpdated(processInformation)));
        ControllerExecutor.getInstance().getEventManager().callEvent(new ProcessUpdatedEvent(processInformation));
    }

    @Override
    public void onChannelClose(String name) {
        final ProcessInformation info = getProcess(name);
        if (info != null) {
            DefaultChannelManager.INSTANCE.get(info.getProcessDetail().getParentName()).ifPresent(packetSender -> packetSender.sendPacket(
                    new ControllerPacketProcessDisconnected(info.getProcessDetail().getProcessUniqueID()))
            );

            System.out.println(LanguageManager.get(
                    "process-connection-lost",
                    info.getProcessDetail().getName(),
                    info.getProcessDetail().getProcessUniqueID(),
                    info.getProcessDetail().getParentName()
            ));
        } else {
            //If the channel is not a process it may be a client
            onClientDisconnect(name);
        }
    }

    @Override
    public void unregisterProcess(UUID uniqueID) {
        ProcessInformation information = getProcess(uniqueID);
        if (information == null) {
            return;
        }

        notifyDisconnect(information);
        processInformation.remove(information);
    }

    // ==========================
    private void notifyDisconnect(ProcessInformation processInformation) {
        DefaultChannelManager.INSTANCE
                .getAllSender()
                .forEach(packetSender -> packetSender.sendPacket(new EventPacketProcessClosed(processInformation)));
        ControllerExecutor.getInstance().getEventManager().callEvent(new ProcessStoppedEvent(processInformation));
    }

    private List<ProcessInformation> getPreparedProcesses(String group) {
        return Streams.list(ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(group),
                e -> e.getProcessDetail().getProcessState().equals(ProcessState.PREPARED)
        );
    }
}
