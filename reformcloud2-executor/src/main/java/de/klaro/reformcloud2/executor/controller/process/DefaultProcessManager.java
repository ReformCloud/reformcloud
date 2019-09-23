package de.klaro.reformcloud2.executor.controller.process;

import de.klaro.reformcloud2.executor.api.common.api.basic.events.ProcessStartedEvent;
import de.klaro.reformcloud2.executor.api.common.api.basic.events.ProcessStoppedEvent;
import de.klaro.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import de.klaro.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.groups.ProcessGroup;
import de.klaro.reformcloud2.executor.api.common.groups.utils.RuntimeConfiguration;
import de.klaro.reformcloud2.executor.api.common.groups.utils.Template;
import de.klaro.reformcloud2.executor.api.common.groups.utils.Version;
import de.klaro.reformcloud2.executor.api.common.language.LanguageManager;
import de.klaro.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import de.klaro.reformcloud2.executor.api.common.process.NetworkInfo;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.common.process.ProcessRuntimeInformation;
import de.klaro.reformcloud2.executor.api.common.process.ProcessState;
import de.klaro.reformcloud2.executor.api.common.utility.list.Links;
import de.klaro.reformcloud2.executor.api.common.utility.list.Trio;
import de.klaro.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import de.klaro.reformcloud2.executor.api.controller.process.ProcessManager;
import de.klaro.reformcloud2.executor.controller.ControllerExecutor;
import de.klaro.reformcloud2.executor.controller.packet.out.ControllerPacketOutProcessDisconnected;
import de.klaro.reformcloud2.executor.controller.packet.out.ControllerPacketOutStartProcess;
import de.klaro.reformcloud2.executor.controller.packet.out.ControllerPacketOutStopProcess;
import de.klaro.reformcloud2.executor.controller.packet.out.event.ControllerEventProcessClosed;
import de.klaro.reformcloud2.executor.controller.packet.out.event.ControllerEventProcessStarted;
import de.klaro.reformcloud2.executor.controller.packet.out.event.ControllerEventProcessUpdated;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;

public final class DefaultProcessManager implements ProcessManager {

    private final List<ProcessInformation> processInformation = new ArrayList<>();

    private Queue<Trio<ProcessGroup, Template, JsonConfiguration>> noClientTryLater = new ConcurrentLinkedQueue<>();

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
        synchronized (processInformation) {
            return Links.newList(processInformation);
        }
    }

    @Override
    public List<ProcessInformation> getProcesses(String group) {
        return Links.list(getAllProcesses(), processInformation -> processInformation.getProcessGroup().getName().equals(group));
    }

    @Override
    public Integer getOnlineAndWaitingProcessCount(String group) {
        int out = noClientTryLater.stream().filter(e -> e.getFirst().getName().equals(group)).mapToInt(value -> 1).sum();
        return getProcesses(group).size() + out;
    }

    @Override
    public ProcessInformation getProcess(String name) {
        requireNonNull(name);
        return Links.filter(processInformation, processInformation -> processInformation.getName().equals(name));
    }

    @Override
    public ProcessInformation getProcess(UUID uniqueID) {
        requireNonNull(uniqueID);
        return Links.filter(processInformation, processInformation -> processInformation.getProcessUniqueID().equals(uniqueID));
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
    public ProcessInformation startProcess(String groupName, String template, JsonConfiguration configurable) {
        ProcessGroup processGroup = Links.filter(ControllerExecutor.getInstance().getControllerExecutorConfig().getProcessGroups(), processGroup1 -> processGroup1.getName().equals(groupName));
        if (processGroup == null) {
            System.out.println(groupName);
            new NullPointerException("Cannot find specified group name").printStackTrace();
            return null;
        }

        Template found = Links.filter(processGroup.getTemplates(), test -> Objects.equals(test.getName(), template));

        ProcessInformation processInformation = create(processGroup, found, configurable);
        if (processInformation == null) {
            return null;
        }

        this.update(processInformation);
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
        if (!processInformation.getNetworkInfo().isConnected()) {
            // If the process is not connected to the controller it will not lose the connection so we are going to remove it
            synchronized (this.processInformation) {
                this.processInformation.remove(processInformation);
            }
        }

        return processInformation;
    }

    @Override
    public void onClientDisconnect(String clientName) {
        Links.allOf(processInformation, processInformation -> processInformation.getParent().equals(clientName)).forEach(processInformation -> {
            synchronized (DefaultProcessManager.this.processInformation) {
                DefaultProcessManager.this.processInformation.remove(processInformation);
            }

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
                bestTemplate.set(new Template(0, "default", "#", null, "§8A ReformCloud2 default Process", new RuntimeConfiguration(
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

        if (template.getServerNameSplitter() != null) {
            stringBuilder.append(template.getServerNameSplitter());
        }

        if (processGroup.isShowIdInName()) {
            stringBuilder.append(id);
        }

        ProcessInformation processInformation = new ProcessInformation(
                stringBuilder.substring(0),
                client.getName(),
                UUID.randomUUID(),
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
        Collection<Integer> ids = Links.newCollection(processInformation, processInformation -> processInformation.getProcessGroup().getName().equals(processGroup.getName()), ProcessInformation::getId);

        while (ids.contains(id)) {
            id++;
        }

        return id;
    }

    private int nextPort(ProcessGroup processGroup) {
        int port = processGroup.getStartupConfiguration().getStartPort();
        Collection<Integer> ports = Links.newCollection(processInformation, processInformation -> processInformation.getNetworkInfo().getPort());

        while (ports.contains(port)) {
            port++;
        }

        return port;
    }

    private ClientRuntimeInformation client(ProcessGroup processGroup, Template template) {
        if (processGroup.getStartupConfiguration().isSearchBestClientAlone()) {
            AtomicReference<ClientRuntimeInformation> best = new AtomicReference<>();
            Links.newCollection(ClientManager.INSTANCE.clientRuntimeInformation, clientRuntimeInformation -> {
                Collection<Integer> startedOn = Links.newCollection(processInformation, processInformation -> processInformation.getParent().equals(clientRuntimeInformation.getName()), processInformation -> processInformation.getTemplate().getRuntimeConfiguration().getMaxMemory());

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
            Links.newCollection(ClientManager.INSTANCE.clientRuntimeInformation, clientRuntimeInformation -> {
                if (!processGroup.getStartupConfiguration().getUseOnlyTheseClients().contains(clientRuntimeInformation.getName())) {
                    return false;
                }

                Collection<Integer> startedOn = Links.newCollection(processInformation, processInformation -> processInformation.getParent().equals(clientRuntimeInformation.getName()), processInformation -> processInformation.getTemplate().getRuntimeConfiguration().getMaxMemory());

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
        return Links.newList(processInformation).iterator();
    }

    @Override
    public void update(ProcessInformation processInformation) {
        synchronized (processInformation) {
            ProcessInformation current = getProcess(processInformation.getProcessUniqueID());
            if (current == null) {
                this.processInformation.add(processInformation);
                return;
            }

            this.processInformation.replaceAll(info -> {
                if (info.getProcessUniqueID().equals(processInformation.getProcessUniqueID())) {
                    info = processInformation;
                }

                return info;
            });
        }

        DefaultChannelManager.INSTANCE.getAllSender().forEach(packetSender -> packetSender.sendPacket(new ControllerEventProcessUpdated(processInformation)));
        ControllerExecutor.getInstance().getEventManager().callEvent(new ProcessUpdatedEvent(processInformation));
    }

    @Override
    public void onChannelClose(String name) {
        final ProcessInformation info = getProcess(name);
        if (info != null) {
            synchronized (processInformation) {
                processInformation.remove(info);
            }

            notifyDisconnect(info);
            DefaultChannelManager.INSTANCE.get(info.getParent()).ifPresent(packetSender -> packetSender.sendPacket(new ControllerPacketOutProcessDisconnected(info.getProcessUniqueID())));

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

    // ==========================
    private void notifyDisconnect(ProcessInformation processInformation) {
        DefaultChannelManager.INSTANCE.getAllSender().forEach(packetSender -> packetSender.sendPacket(new ControllerEventProcessClosed(processInformation)));
        ControllerExecutor.getInstance().getEventManager().callEvent(new ProcessStoppedEvent(processInformation));
    }
}
