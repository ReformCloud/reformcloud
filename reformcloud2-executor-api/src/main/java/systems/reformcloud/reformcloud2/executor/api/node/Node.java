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
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.WrappedByteInput;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.server.NetworkServer;
import systems.reformcloud.reformcloud2.executor.api.common.utility.runtime.ReloadableRuntime;

import javax.annotation.Nonnull;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.util.function.Consumer;

public abstract class Node extends ExecutorAPI implements ReloadableRuntime {

    protected abstract void bootstrap();

    public abstract void shutdown() throws Exception;

    @Nonnull
    public static Node getInstance() {
        return (Node) ExecutorAPI.getInstance();
    }

    @Nonnull
    public abstract NetworkServer getNetworkServer();

    @Nonnull
    public abstract CommandManager getCommandManager();

    @Nonnull
    protected final NetworkChannelReader createReader(@Nonnull Consumer<PacketSender> onDisconnect) {
        return new NetworkChannelReader() {

            private PacketSender sender;

            @Nonnull
            @Override
            public PacketHandler getPacketHandler() {
                return Node.this.getPacketHandler();
            }

            @Nonnull
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
                if (sender != null) {
                    onDisconnect.accept(sender);
                    DefaultChannelManager.INSTANCE.unregisterChannel(sender);
                    System.out.println(LanguageManager.get("network-channel-disconnected", sender.getName()));
                }
            }

            @Override
            public void read(ChannelHandlerContext context, WrappedByteInput input) {
                NetworkUtil.EXECUTOR.execute(() ->
                        getPacketHandler().getNetworkHandlers(input.getPacketID()).forEach(networkHandler -> {
                            try (ObjectInputStream stream = input.toObjectStream()) {
                                Packet packet = networkHandler.read(input.getPacketID(), stream);

                                networkHandler.handlePacket(sender, packet, out -> {
                                    if (packet.queryUniqueID() != null) {
                                        out.setQueryID(packet.queryUniqueID());
                                        sender.sendPacket(out);
                                    }
                                });
                            } catch (final Exception ex) {
                                ex.printStackTrace();
                            }
                        })
                );
            }
        };
    }
}
