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
package systems.refomcloud.reformcloud2.embedded.nukkit;

import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.reformcloud2.embedded.executor.PlayerAPIExecutor;
import systems.refomcloud.reformcloud2.embedded.network.api.*;
import systems.refomcloud.reformcloud2.embedded.network.channel.APIEndpointChannelReader;
import systems.refomcloud.reformcloud2.embedded.network.packets.out.APIPacketOutRequestIngameMessages;
import systems.refomcloud.reformcloud2.embedded.network.packets.out.APIPacketOutRequestIngameMessagesResult;
import systems.refomcloud.reformcloud2.embedded.nukkit.executor.NukkitPlayerAPIExecutor;
import systems.refomcloud.reformcloud2.embedded.shared.SharedInvalidPlayerFixer;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.api.basic.events.ExternalEventBusHandler;
import systems.reformcloud.reformcloud2.executor.api.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.groups.messages.IngameMessages;
import systems.reformcloud.reformcloud2.executor.api.io.IOUtils;
import systems.reformcloud.reformcloud2.executor.api.network.challenge.shared.ClientChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.network.client.NetworkClient;
import systems.reformcloud.reformcloud2.executor.api.network.packet.PacketProvider;
import systems.reformcloud.reformcloud2.executor.api.network.packet.defaults.DefaultPacketProvider;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.shared.event.DefaultEventManager;
import systems.reformcloud.reformcloud2.shared.network.client.DefaultNetworkClient;

import java.io.File;

public final class NukkitExecutor extends API {

    private static NukkitExecutor instance;

    private final Plugin plugin;

    private final PacketProvider packetProvider = new DefaultPacketProvider();

    private final NetworkClient networkClient = new DefaultNetworkClient();

    private IngameMessages messages = new IngameMessages();

    private ProcessInformation thisProcessInformation;

    NukkitExecutor(Plugin plugin) {
        super.type = ExecutorType.API;
        super.loadPacketHandlers();
        PlayerAPIExecutor.setInstance(new NukkitPlayerAPIExecutor());

        instance = this;
        this.plugin = plugin;

        new ExternalEventBusHandler(this.packetProvider, new DefaultEventManager());
        this.getEventManager().registerListener(this);

        this.packetProvider.registerNetworkHandlers(
                PacketAPIPlayEntityEffect.class,
                PacketAPIPlaySound.class,
                PacketAPIKickPlayer.class,
                PacketAPISendMessage.class,
                PacketAPISendTitle.class,
                PacketAPITeleportPlayer.class,
                APIPacketOutRequestIngameMessagesResult.class
        );

        String connectionKey = JsonConfiguration.read("reformcloud/.connection/key.json").getString("key");
        IOUtils.deleteFile(new File("reformcloud/.connection/key.json"));
        JsonConfiguration connectionConfig = JsonConfiguration.read("reformcloud/.connection/connection.json");

        this.thisProcessInformation = connectionConfig.get("startInfo", ProcessInformation.TYPE);
        if (this.thisProcessInformation == null) {
            System.exit(0);
            return;
        }

        this.networkClient.connect(
                connectionConfig.getString("controller-host"),
                connectionConfig.getInteger("controller-port"),
                () -> new APIEndpointChannelReader(),
                new ClientChallengeAuthHandler(
                        connectionKey,
                        this.thisProcessInformation.getProcessDetail().getName(),
                        () -> new JsonConfiguration(),
                        context -> {
                        } // unused here
                )
        );
        ExecutorAPI.setInstance(this);
        this.awaitConnectionAndUpdate();
    }

    @NotNull
    public static NukkitExecutor getInstance() {
        return instance;
    }

    NetworkClient getNetworkClient() {
        return this.networkClient;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    @NotNull
    public IngameMessages getMessages() {
        return this.messages;
    }

    public void setMessages(IngameMessages messages) {
        this.messages = messages;
    }

    @NotNull
    public EventManager getEventManager() {
        return ExternalEventBusHandler.getInstance().getEventManager();
    }

    @Override
    public PacketProvider packetHandler() {
        return this.packetProvider;
    }

    @NotNull
    @Override
    public ProcessInformation getCurrentProcessInformation() {
        return this.thisProcessInformation;
    }

    public void setThisProcessInformation(ProcessInformation thisProcessInformation) {
        this.thisProcessInformation = thisProcessInformation;
    }

    private void awaitConnectionAndUpdate() {
        Task.EXECUTOR.execute(() -> {
            PacketSender packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
            while (packetSender == null) {
                packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
                AbsoluteThread.sleep(100);
            }

            this.thisProcessInformation.updateMaxPlayers(Server.getInstance().getMaxPlayers());
            this.thisProcessInformation.updateRuntimeInformation();
            this.thisProcessInformation.getNetworkInfo().setConnected(true);
            this.thisProcessInformation.getProcessDetail().setProcessState(this.thisProcessInformation.getProcessDetail().getInitialState());
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(this.thisProcessInformation);

            this.fixInvalidPlayers();

            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(controller -> this.packetProvider.getQueryHandler().sendQueryAsync(
                    controller,
                    new APIPacketOutRequestIngameMessages()
                    ).onComplete(packet -> {
                        if (packet instanceof APIPacketOutRequestIngameMessagesResult) {
                            this.setMessages(((APIPacketOutRequestIngameMessagesResult) packet).getIngameMessages());
                        }
                    })
            );
        });
    }

    private void fixInvalidPlayers() {
        SharedInvalidPlayerFixer.start(
                uuid -> Server.getInstance().getPlayer(uuid).isPresent(),
                () -> Server.getInstance().getOnlinePlayers().size(),
                information -> this.thisProcessInformation = information
        );
    }

    @Listener
    public void handle(final ProcessUpdatedEvent event) {
        if (event.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(this.thisProcessInformation.getProcessDetail().getProcessUniqueID())) {
            this.thisProcessInformation = event.getProcessInformation();
        }
    }
}
