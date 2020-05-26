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
package systems.reformcloud.reformcloud2.executor.api.sponge;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
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
import systems.reformcloud.reformcloud2.executor.api.sponge.event.PlayerListenerHandler;
import systems.reformcloud.reformcloud2.executor.api.sponge.executor.SpongePlayerExecutor;

import java.io.File;

public class SpongeExecutor extends API {

    private static SpongeExecutor instance;

    private final PacketHandler packetHandler = new DefaultPacketHandler();

    private final NetworkClient networkClient = new DefaultNetworkClient();

    private final SpongeLauncher plugin;

    private IngameMessages messages = new IngameMessages();

    private ProcessInformation thisProcessInformation;

    SpongeExecutor(SpongeLauncher launcher) {
        super.type = ExecutorType.API;
        super.loadPacketHandlers();
        PlayerAPIExecutor.setInstance(new SpongePlayerExecutor());

        this.plugin = launcher;
        instance = this;

        this.packetHandler.registerNetworkHandlers(
                PacketAPIKickPlayer.class,
                PacketAPISendMessage.class,
                PacketAPISendTitle.class,
                PacketAPIPlaySound.class,
                PacketAPITeleportPlayer.class,
                APIPacketOutRequestIngameMessagesResult.class
        );

        new ExternalEventBusHandler(this.packetHandler, new DefaultEventManager());
        this.getEventManager().registerListener(this);
        Sponge.getEventManager().registerListeners(launcher, new PlayerListenerHandler());

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
    public static SpongeExecutor getInstance() {
        return instance;
    }

    public IngameMessages getMessages() {
        return this.messages;
    }

    public void setMessages(IngameMessages messages) {
        this.messages = messages;
    }

    @Override
    public PacketHandler packetHandler() {
        return this.packetHandler;
    }

    @NotNull
    @Override
    public EventManager getEventManager() {
        return ExternalEventBusHandler.getInstance().getEventManager();
    }

    @NotNull
    public NetworkClient getNetworkClient() {
        return this.networkClient;
    }

    @NotNull
    public SpongeLauncher getPlugin() {
        return this.plugin;
    }

    @NotNull
    @Override
    public ProcessInformation getCurrentProcessInformation() {
        return this.thisProcessInformation;
    }

    public void setThisProcessInformation(@NotNull ProcessInformation thisProcessInformation) {
        this.thisProcessInformation = thisProcessInformation;
    }

    @Listener
    public void handle(final ProcessUpdatedEvent event) {
        if (event.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(this.thisProcessInformation.getProcessDetail().getProcessUniqueID())) {
            this.thisProcessInformation = event.getProcessInformation();
        }
    }

    private void awaitConnectionAndUpdate() {
        Task.EXECUTOR.execute(() -> {
            PacketSender packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
            while (packetSender == null) {
                packetSender = DefaultChannelManager.INSTANCE.get("Controller").orElse(null);
                AbsoluteThread.sleep(100);
            }

            this.thisProcessInformation.updateMaxPlayers(Sponge.getServer().getMaxPlayers());
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
            }));
        });
    }

    private void fixInvalidPlayers() {
        SharedInvalidPlayerFixer.start(
                uuid -> Sponge.getServer().getPlayer(uuid).isPresent(),
                () -> Sponge.getServer().getOnlinePlayers().size(),
                information -> this.thisProcessInformation = information
        );
    }
}
