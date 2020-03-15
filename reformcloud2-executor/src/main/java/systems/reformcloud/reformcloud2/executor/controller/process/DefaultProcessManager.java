package systems.reformcloud.reformcloud2.executor.controller.process;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStartedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStoppedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.RuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.basic.FileBackend;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.NetworkInfo;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.process.util.MemoryCalculator;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Trio;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.api.controller.process.ProcessManager;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;
import systems.reformcloud.reformcloud2.executor.controller.network.packets.out.ControllerPacketOutProcessDisconnected;
import systems.reformcloud.reformcloud2.executor.controller.network.packets.out.ControllerPacketOutStartProcess;
import systems.reformcloud.reformcloud2.executor.controller.network.packets.out.ControllerPacketOutStopProcess;
import systems.reformcloud.reformcloud2.executor.controller.network.packets.out.event.ControllerEventProcessClosed;
import systems.reformcloud.reformcloud2.executor.controller.network.packets.out.event.ControllerEventProcessStarted;
import systems.reformcloud.reformcloud2.executor.controller.network.packets.out.event.ControllerEventProcessUpdated;
import systems.reformcloud.reformcloud2.executor.node.util.ProcessCopyOnWriteArrayList;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;

public final class DefaultProcessManager implements ProcessManager {

    private final Collection<ProcessInformation> processInformation = Collections.synchronizedCollection(new ProcessCopyOnWriteArrayList());

    private final Queue<Trio<ProcessGroup, Template, JsonConfiguration>> noClientTryLater = new ConcurrentLinkedQueue<>();

    public DefaultProcessManager() {
        CompletableFuture.runAsync(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (!noClientTryLater.isEmpty()) {
                    Trio<ProcessGroup, Template, JsonConfiguration> trio = noClientTryLater.peek();
                    startProcess(trio.getFirst().getName(), trio.getSecond().getName(), trio.getThird());
                    noClientTryLater.remove(trio);
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
    public Integer getOnlineAndWaitingProcessCount(String group) {
        int out = noClientTryLater.stream().filter(e -> e.getFirst().getName().equals(group)).mapToInt(value -> 1).sum();
        return getProcesses(group).size() + out;
    }

    @Override
    public ProcessInformation getProcess(String name) {
        requireNonNull(name);
        return Streams.filter(processInformation, processInformation -> processInformation.getName().equals(name));
    }

    @Override
    public ProcessInformation getProcess(UUID uniqueID) {
        requireNonNull(uniqueID);
        return Streams.filter(processInformation, processInformation -> processInformation.getProcessUniqueID().equals(uniqueID));
    }

    @Override
    public ProcessInformation startProcess(String groupName) {
        requireNonNull(groupName);
        return startProcess(groupName, null);
    }

    @Override
    public ProcessInformation startProcess(String groupName, String template) {
        return startProcess(groupName, template, null);
    }

    @Override
    public synchronized ProcessInformation startProcess(String groupName, String template, JsonConfiguration configurable) {
        ProcessGroup processGroup = Streams.filter(ControllerExecutor.getInstance().getControllerExecutorConfig().getProcessGroups(), processGroup1 -> processGroup1.getName().equals(groupName));
        if (processGroup == null) {
            // In some cases the group got deleted but the update process is sync and this method get called async!
            // To prevent any issues just return at this point
            return null;
        }

        Template found = Streams.filter(processGroup.getTemplates(), test -> template == null || template.equals(test.getName()));
        ProcessInformation processInformation = this.create(processGroup, found, configurable);
        if (processInformation == null) {
            return null;
        }

        this.processInformation.add(processInformation);
        DefaultChannelManager.INSTANCE.get(processInformation.getParent()).ifPresent(packetSender -> packetSender.sendPacket(new ControllerPacketOutStartProcess(processInformation)));
        //Send event packet to notify processes
        DefaultChannelManager.INSTANCE.getAllSender().forEach(packetSender -> packetSender.sendPacket(new ControllerEventProcessStarted(processInformation)));
        ControllerExecutor.getInstance().getEventManager().callEvent(new ProcessStartedEvent(processInformation));
        return processInformation;
    }

    @Override
    public ProcessInformation stopProcess(String name) {
        ProcessInformation processInformation = getProcess(name);
        if (processInformation == null) {
            return null;
        }

        return stopProcess(processInformation.getProcessUniqueID());
    }

    @Override
    public ProcessInformation stopProcess(UUID uniqueID) {
        ProcessInformation processInformation = getProcess(uniqueID);
        if (processInformation == null) {
            return null;
        }

        DefaultChannelManager.INSTANCE.get(processInformation.getParent()).ifPresent(packetSender -> packetSender.sendPacket(new ControllerPacketOutStopProcess(processInformation.getProcessUniqueID())));
        return processInformation;
    }

    @Override
    public void onClientDisconnect(String clientName) {
        Streams.allOf(processInformation, processInformation -> processInformation.getParent().equals(clientName)).forEach(processInformation -> {
            DefaultProcessManager.this.processInformation.remove(processInformation);
            notifyDisconnect(processInformation);
        });
    }

    private ProcessInformation create(ProcessGroup processGroup, Template template, JsonConfiguration extra) {
        if (extra == null) {
            extra = new JsonConfiguration();
        }

        if (template == null) {
            AtomicReference<Template> bestTemplate = new AtomicReference<>();
            processGroup.getTemplates().forEach(template1 -> {
                if (bestTemplate.get() == null) {
                    bestTemplate.set(template1);
                } else {
                    if (bestTemplate.get().getPriority() < template1.getPriority()) {
                        bestTemplate.set(template1);
                    }
                }
            });

            if (bestTemplate.get() == null) {
                bestTemplate.set(new Template(0, "default", false, FileBackend.NAME, "#", new RuntimeConfiguration(
                        512, new ArrayList<>(), new HashMap<>()
                ), Version.PAPER_1_8_8));

                System.err.println("Starting up process " + processGroup.getName() + " with default template because no template is set up");
                Thread.dumpStack();
            }

            template = bestTemplate.get();
        }

        ClientRuntimeInformation client = client(processGroup, template);
        if (client == null) {
            noClientTryLater.add(new Trio<>(processGroup, template, extra));
            return null;
        }

        int id = nextID(processGroup);
        int port = nextPort(processGroup);
        StringBuilder stringBuilder = new StringBuilder().append(processGroup.getName());

        if (processGroup.isShowIdInName()) {
            if (template.getServerNameSplitter() != null) {
                stringBuilder.append(template.getServerNameSplitter());
            }

            stringBuilder.append(id);
        }

        ProcessInformation processInformation = new ProcessInformation(
                processGroup.getName() + template.getServerNameSplitter() + id,
                stringBuilder.substring(0),
                client.getName(),
                null,
                UUID.randomUUID(),
                MemoryCalculator.calcMemory(processGroup.getName(), template),
                id,
                ProcessState.PREPARED,
                new NetworkInfo(
                        client.startHost(),
                        port,
                        false
                ), processGroup, template, ProcessRuntimeInformation.empty(), new ArrayList<>(), extra, 0
        );
        return processInformation.updateMaxPlayers(null);
    }

    private int nextID(ProcessGroup processGroup) {
        int id = 1;
        Collection<Integer> ids = Streams.newCollection(processInformation, processInformation -> processInformation.getProcessGroup().getName().equals(processGroup.getName()), ProcessInformation::getId);

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

    private ClientRuntimeInformation client(ProcessGroup processGroup, Template template) {
        if (processGroup.getStartupConfiguration().isSearchBestClientAlone()) {
            AtomicReference<ClientRuntimeInformation> best = new AtomicReference<>();
            Streams.newCollection(ClientManager.INSTANCE.getClientRuntimeInformation(), clientRuntimeInformation -> {
                Collection<Integer> startedOn = Streams.newCollection(processInformation, processInformation -> processInformation.getParent().equals(clientRuntimeInformation.getName()), processInformation -> processInformation.getTemplate().getRuntimeConfiguration().getMaxMemory());

                int usedMemory = 0;
                for (Integer integer : startedOn) {
                    usedMemory += integer;
                }

                if (startedOn.size() < clientRuntimeInformation.maxProcessCount() || clientRuntimeInformation.maxProcessCount() == -1) {
                    return clientRuntimeInformation.maxMemory() > (usedMemory + template.getRuntimeConfiguration().getMaxMemory());
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

                Collection<Integer> startedOn = Streams.newCollection(processInformation, processInformation -> processInformation.getParent().equals(clientRuntimeInformation.getName()), processInformation -> processInformation.getTemplate().getRuntimeConfiguration().getMaxMemory());

                int usedMemory = 0;
                for (Integer integer : startedOn) {
                    usedMemory += integer;
                }

                if (startedOn.size() < clientRuntimeInformation.maxProcessCount() || clientRuntimeInformation.maxProcessCount() == -1) {
                    return clientRuntimeInformation.maxMemory() > (usedMemory + template.getRuntimeConfiguration().getMaxMemory());
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
    public void update(@Nonnull ProcessInformation processInformation) {
        synchronized (processInformation) {
            ProcessInformation current = getProcess(processInformation.getProcessUniqueID());
            if (current == null) {
                return;
            }

            Streams.filterToReference(this.processInformation,
                    e -> e.getProcessUniqueID().equals(processInformation.getProcessUniqueID())).ifPresent(e -> {
                this.processInformation.remove(e);
                this.processInformation.add(processInformation);
            });
        }

        DefaultChannelManager.INSTANCE.getAllSender().forEach(packetSender -> packetSender.sendPacket(new ControllerEventProcessUpdated(processInformation)));
        ControllerExecutor.getInstance().getEventManager().callEvent(new ProcessUpdatedEvent(processInformation));
    }

    @Override
    public void onChannelClose(String name) {
        final ProcessInformation info = getProcess(name);
        if (info != null) {
            DefaultChannelManager.INSTANCE.get(info.getParent()).ifPresent(packetSender -> packetSender.sendPacket(
                    new ControllerPacketOutProcessDisconnected(info.getProcessUniqueID()))
            );

            System.out.println(LanguageManager.get(
                    "process-connection-lost",
                    info.getName(),
                    info.getProcessUniqueID(),
                    info.getParent()
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
        DefaultChannelManager.INSTANCE.getAllSender().forEach(packetSender -> packetSender.sendPacket(new ControllerEventProcessClosed(processInformation)));
        ControllerExecutor.getInstance().getEventManager().callEvent(new ProcessStoppedEvent(processInformation));
    }
}
