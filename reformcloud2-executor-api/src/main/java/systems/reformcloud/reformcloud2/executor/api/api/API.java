package systems.reformcloud.reformcloud2.executor.api.api;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.Objects;

public abstract class API extends ExternalAPIImplementation {

    public abstract ProcessInformation getThisProcessInformation();

    protected final NetworkChannelReader networkChannelReader = new NetworkChannelReader() {

        private PacketSender packetSender;

        @Nonnull
        @Override
        public PacketHandler getPacketHandler() {
            return API.this.packetHandler();
        }

        @Nonnull
        @Override
        public PacketSender sender() {
            return packetSender;
        }

        @Override
        public void setSender(PacketSender sender) {
            Conditions.isTrue(packetSender == null);
            packetSender = Objects.requireNonNull(sender);
            if (packetSender.getName().equals("Controller")) {
                getThisProcessInformation().getNetworkInfo().setConnected(true);
                getThisProcessInformation().setProcessState(ProcessState.READY);
            }
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
            if (packetSender != null) {
                DefaultChannelManager.INSTANCE.unregisterChannel(packetSender);
                System.out.println(LanguageManager.get("network-channel-disconnected", packetSender.getName()));
            }
        }

        @Override
        public void read(ChannelHandlerContext context, Packet packet) {
            NetworkUtil.EXECUTOR.execute(() -> {
                if (packet.queryUniqueID() != null && getPacketHandler().getQueryHandler().hasWaitingQuery(packet.queryUniqueID())) {
                    getPacketHandler().getQueryHandler().getWaitingQuery(packet.queryUniqueID()).complete(packet);
                } else {
                    getPacketHandler().getNetworkHandlers(packet.packetID()).forEach(networkHandler -> networkHandler.handlePacket(packetSender, packet, out -> {
                        if (packet.queryUniqueID() != null) {
                            out.setQueryID(packet.queryUniqueID());
                            packetSender.sendPacket(out);
                        }
                    }));
                }
            });
        }
    };
}
