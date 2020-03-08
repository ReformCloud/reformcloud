package systems.reformcloud.reformcloud2.executor.api.common.network.channel.shared;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.defaults.DefaultPacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.WrappedByteInput;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;

import javax.annotation.Nonnull;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;

public abstract class SharedNetworkChannelReader implements NetworkChannelReader {

    public SharedNetworkChannelReader(PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    protected final PacketHandler packetHandler;

    protected PacketSender packetSender;

    @Nonnull
    @Override
    public PacketHandler getPacketHandler() {
        return this.packetHandler;
    }

    @Nonnull
    @Override
    public PacketSender sender() {
        return this.packetSender;
    }

    @Override
    public void setChannelHandlerContext(@Nonnull ChannelHandlerContext channelHandlerContext, @Nonnull String name) {
        Conditions.isTrue(this.packetSender == null, "Cannot redefine packet sender");
        this.packetSender = new DefaultPacketSender(channelHandlerContext);
        this.packetSender.setName(name);
        DefaultChannelManager.INSTANCE.registerChannel(this.packetSender);
    }

    @Override
    public void channelActive(ChannelHandlerContext context) {
        if (packetSender == null) {
            String address = ((InetSocketAddress) context.channel().remoteAddress()).getAddress().getHostAddress();
            System.out.println(LanguageManager.get("network-channel-connected", address));
        }
    }

    @Override
    public void read(ChannelHandlerContext context, WrappedByteInput input) {
        NetworkUtil.EXECUTOR.execute(() ->
                getPacketHandler().getNetworkHandlers(input.getPacketID()).forEach(networkHandler -> {
                    try (ObjectInputStream stream = input.toObjectStream()) {
                        Packet packet = networkHandler.read(input.getPacketID(), stream);

                        networkHandler.handlePacket(packetSender, packet, out -> {
                            if (packet.queryUniqueID() != null) {
                                out.setQueryID(packet.queryUniqueID());
                                packetSender.sendPacket(out);
                            }
                        });
                    } catch (final Exception ex) {
                        ex.printStackTrace();
                    }
                })
        );
    }
}
