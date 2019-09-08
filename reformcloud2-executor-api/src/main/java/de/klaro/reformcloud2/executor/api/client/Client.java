package de.klaro.reformcloud2.executor.api.client;

import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.base.Conditions;
import de.klaro.reformcloud2.executor.api.common.commands.manager.CommandManager;
import de.klaro.reformcloud2.executor.api.common.language.LanguageManager;
import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import de.klaro.reformcloud2.executor.api.common.network.client.NetworkClient;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import de.klaro.reformcloud2.executor.api.common.utility.runtime.ReloadableRuntime;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class Client extends ExternalAPIImplementation implements ReloadableRuntime {

    protected abstract void bootstrap();

    public abstract void shutdown() throws Exception;

    public abstract CommandManager getCommandManager();

    public static Client getInstance() {
        return (Client) ExecutorAPI.getInstance();
    }

    public abstract NetworkClient getNetworkClient();

    protected final NetworkChannelReader createChannelReader(Runnable onDisconnect) {
        return new NetworkChannelReader() {

            private PacketSender packetSender;

            @Override
            public PacketHandler getPacketHandler() {
                return Client.this.packetHandler();
            }

            @Override
            public PacketSender sender() {
                return packetSender;
            }

            @Override
            public void setSender(PacketSender sender) {
                Conditions.isTrue(packetSender == null);
                packetSender = Objects.requireNonNull(sender);
                DefaultChannelManager.INSTANCE.registerChannel(packetSender);
            }

            @Override
            public void channelActive(ChannelHandlerContext context) {
                if (packetSender == null) {
                    String address = ((InetSocketAddress) context.channel().remoteAddress()).getAddress().getHostAddress();
                    System.out.println(LanguageManager.get("network-channel-connected", address));
                }
            }

            @Override
            public void channelInactive(ChannelHandlerContext context) {
                onDisconnect.run();
                if (packetSender != null) {
                    DefaultChannelManager.INSTANCE.unregisterChannel(packetSender);
                    System.out.println(LanguageManager.get("network-channel-disconnected", packetSender.getName()));
                }
            }

            @Override
            public void read(ChannelHandlerContext context, Packet packet) {
                NetworkUtil.EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (packet.queryUniqueID() != null && getPacketHandler().getQueryHandler().hasWaitingQuery(packet.queryUniqueID())) {
                            getPacketHandler().getQueryHandler().getWaitingQuery(packet.queryUniqueID()).complete(packet);
                        } else {
                            getPacketHandler().getNetworkHandlers(packet.packetID()).forEach(new Consumer<NetworkHandler>() {
                                @Override
                                public void accept(NetworkHandler networkHandler) {
                                    networkHandler.handlePacket(packetSender, packet, new Consumer<Packet>() {
                                        @Override
                                        public void accept(Packet out) {
                                            if (packet.queryUniqueID() != null) {
                                                out.setQueryID(packet.queryUniqueID());
                                                packetSender.sendPacket(out);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        };
    }
}
