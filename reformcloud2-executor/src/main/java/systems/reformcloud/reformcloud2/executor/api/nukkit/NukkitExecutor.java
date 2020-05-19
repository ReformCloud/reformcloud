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
package systems.reformcloud.reformcloud2.executor.api.nukkit;

import cn.nukkit.Server;
import cn.nukkit.level.Location;
import cn.nukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.APIConstants;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ExternalEventBusHandler;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.common.event.basic.DefaultEventManager;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.groups.messages.IngameMessages;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.shared.ClientChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.DefaultNetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.NetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults.DefaultPacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.api.executor.PlayerAPIExecutor;
import systems.reformcloud.reformcloud2.executor.api.network.api.*;
import systems.reformcloud.reformcloud2.executor.api.network.channel.APINetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.network.packets.out.APIPacketOutRequestIngameMessages;
import systems.reformcloud.reformcloud2.executor.api.network.packets.out.APIPacketOutRequestIngameMessagesResult;
import systems.reformcloud.reformcloud2.executor.api.shared.SharedInvalidPlayerFixer;

import java.io.File;
import java.util.UUID;

public final class NukkitExecutor extends API implements PlayerAPIExecutor {

    private static NukkitExecutor instance;

    private final Plugin plugin;

    private final PacketHandler packetHandler = new DefaultPacketHandler();

    private final NetworkClient networkClient = new DefaultNetworkClient();

    private IngameMessages messages = new IngameMessages();

    private ProcessInformation thisProcessInformation;

    NukkitExecutor(Plugin plugin) {
        super.type = ExecutorType.API;
        super.loadPacketHandlers();
        APIConstants.playerAPIExecutor = this;

        instance = this;
        this.plugin = plugin;

        new ExternalEventBusHandler(this.packetHandler, new DefaultEventManager());
        this.getEventManager().registerListener(this);

        this.packetHandler.registerNetworkHandlers(
                PacketAPIPlayEntityEffect.class,
                PacketAPIPlaySound.class,
                PacketAPIKickPlayer.class,
                PacketAPISendMessage.class,
                PacketAPISendTitle.class,
                PacketAPITeleportPlayer.class,
                APIPacketOutRequestIngameMessagesResult.class
        );

        String connectionKey = JsonConfiguration.read("reformcloud/.connection/key.json").getString("key");
        SystemHelper.deleteFile(new File("reformcloud/.connection/key.json"));
        JsonConfiguration connectionConfig = JsonConfiguration.read("reformcloud/.connection/connection.json");

        this.thisProcessInformation = connectionConfig.get("startInfo", ProcessInformation.TYPE);
        if (this.thisProcessInformation == null) {
            System.exit(0);
            return;
        }

        this.networkClient.connect(
                connectionConfig.getString("controller-host"),
                connectionConfig.getInteger("controller-port"),
                () -> new APINetworkChannelReader(),
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
    public PacketHandler packetHandler() {
        return this.packetHandler;
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

            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(controller -> this.packetHandler.getQueryHandler().sendQueryAsync(
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

    /* ======================== Player API ======================== */

    @Override
    public void executeSendMessage(UUID player, String message) {
        Server.getInstance().getPlayer(player).ifPresent(player1 -> player1.sendMessage(message));
    }

    @Override
    public void executeKickPlayer(UUID player, String message) {
        Server.getInstance().getPlayer(player).ifPresent(player1 -> player1.kick(message));
    }

    @Override
    public void executePlaySound(UUID player, String sound, float f1, float f2) {
        throw new UnsupportedOperationException("Not supported on nukkit");
    }

    @Override
    public void executeSendTitle(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        Server.getInstance().getPlayer(player).ifPresent(player1 -> player1.sendTitle(title, subTitle, fadeIn, stay, fadeOut));
    }

    @Override
    public void executePlayEffect(UUID player, String entityEffect) {
        throw new UnsupportedOperationException("Not supported on nukkit");
    }

    @Override
    public void executeTeleport(UUID player, String world, double x, double y, double z, float yaw, float pitch) {
        Server.getInstance().getPlayer(player).ifPresent(player1 -> player1.teleport(new Location(x, y, z, yaw, pitch)));
    }

    @Override
    public void executeConnect(UUID player, String server) {
        throw new UnsupportedOperationException("Not supported on nukkit");
    }

    @Override
    public void executeConnect(UUID player, ProcessInformation server) {
        throw new UnsupportedOperationException("Not supported on nukkit");
    }

    @Override
    public void executeConnect(UUID player, UUID target) {
        throw new UnsupportedOperationException("Not supported on nukkit");
    }
}
