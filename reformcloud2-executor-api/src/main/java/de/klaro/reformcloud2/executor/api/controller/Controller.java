package de.klaro.reformcloud2.executor.api.controller;

import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.base.Conditions;
import de.klaro.reformcloud2.executor.api.common.commands.manager.CommandManager;
import de.klaro.reformcloud2.executor.api.common.language.LanguageManager;
import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import de.klaro.reformcloud2.executor.api.common.network.server.NetworkServer;
import de.klaro.reformcloud2.executor.api.common.utility.runtime.ReloadableRuntime;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

public abstract class Controller extends ExecutorAPI implements ReloadableRuntime {

    protected abstract void bootstrap();

    public abstract void shutdown() throws Exception;

    public static Controller getInstance() {
        return (Controller) ExecutorAPI.getInstance();
    }

    public abstract NetworkServer getNetworkServer();

    public abstract PacketHandler getPacketHandler();

    public abstract CommandManager getCommandManager();

    public NetworkChannelReader networkChannelReader() {
        return new NetworkChannelReader() {

            private PacketSender sender;

            @Override
            public PacketHandler getPacketHandler() {
                return Controller.this.getPacketHandler();
            }

            @Override
            public PacketSender sender() {
                return sender;
            }

            @Override
            public void setSender(PacketSender sender) {
                Conditions.isTrue(this.sender == null);
                this.sender = sender;
                DefaultChannelManager.INSTANCE.registerChannel(sender);
            }

            @Override
            public void channelActive(ChannelHandlerContext context) {
                if (sender == null) {
                    String address = ((InetSocketAddress) context.channel().remoteAddress()).getAddress().getHostAddress();
                    System.out.println(LanguageManager.get("network-channel-connected", address));
                }
            }

            @Override
            public void channelInactive(ChannelHandlerContext context) {
                if (sender != null) {
                    DefaultChannelManager.INSTANCE.unregisterChannel(sender);
                    System.out.println(LanguageManager.get("network-channel-disconnected", sender.getName()));
                }
            }

            @Override
            public void read(ChannelHandlerContext context, Packet packet) {
                NetworkUtil.EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        getPacketHandler().getNetworkHandlers(packet.packetID()).forEach(new Consumer<NetworkHandler>() {
                            @Override
                            public void accept(NetworkHandler networkHandler) {
                                networkHandler.handlePacket(sender, packet, new Consumer<Packet>() {
                                    @Override
                                    public void accept(Packet packet) {
                                        if (packet.queryUniqueID() != null) {
                                            sender.sendPacket(packet);
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        };
    }
}
