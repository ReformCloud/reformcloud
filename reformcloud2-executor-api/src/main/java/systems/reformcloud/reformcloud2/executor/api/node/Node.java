package systems.reformcloud.reformcloud2.executor.api.node;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.server.NetworkServer;
import systems.reformcloud.reformcloud2.executor.api.common.utility.runtime.ReloadableRuntime;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

public abstract class Node extends ExecutorAPI implements ReloadableRuntime {

    public static void setInstance(Node instance) {
        Conditions.isTrue(Node.instance == null);
        Node.instance = instance;
    }

    private static Node instance;

    protected abstract void bootstrap();

    public abstract void shutdown() throws Exception;

    public static Node getInstance() {
        return instance == null ? (Node) ExecutorAPI.getInstance() : instance;
    }

    public abstract NetworkServer getNetworkServer();

    public abstract CommandManager getCommandManager();

    protected final NetworkChannelReader createReader(Consumer<String> onDisconnect) {
        return new NetworkChannelReader() {

            private PacketSender sender;

            @Override
            public PacketHandler getPacketHandler() {
                return Node.this.getPacketHandler();
            }

            @Override
            public PacketSender sender() {
                return sender;
            }

            @Override
            public void setSender(PacketSender sender) {
                Conditions.isTrue(this.sender == null);
                this.sender = sender;
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
                onDisconnect.accept(sender.getName());
                if (sender != null) {
                    DefaultChannelManager.INSTANCE.unregisterChannel(sender);
                    System.out.println(LanguageManager.get("network-channel-disconnected", sender.getName()));
                }
            }

            @Override
            public void read(ChannelHandlerContext context, Packet packet) {
                NetworkUtil.EXECUTOR.execute(() -> {
                    if (packet.queryUniqueID() != null && getPacketHandler().getQueryHandler().hasWaitingQuery(packet.queryUniqueID())) {
                        getPacketHandler().getQueryHandler().getWaitingQuery(packet.queryUniqueID()).complete(packet);
                    } else {
                        getPacketHandler().getNetworkHandlers(packet.packetID()).forEach(networkHandler -> networkHandler.handlePacket(sender, packet, out -> {
                            if (packet.queryUniqueID() != null) {
                                out.setQueryID(packet.queryUniqueID());
                                sender.sendPacket(out);
                            }
                        }));
                    }
                });
            }
        };
    }
}
