package systems.reformcloud.reformcloud2.executor.api.api;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.WrappedByteInput;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;

import javax.annotation.Nonnull;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * This class can only get called if the environment is {@link systems.reformcloud.reformcloud2.executor.api.ExecutorType#API}.
 * Check this by using {@link ExecutorAPI#getType()}. If the current instance is not an api instance
 * just use the default cloud api based on {@link ExecutorAPI#getInstance()}.
 */
public abstract class API extends ExternalAPIImplementation {

    /**
     * @return The current process information the current api instance is using
     */
    @Nonnull
    public abstract ProcessInformation getCurrentProcessInformation();

    /**
     * @return The current api instance the cloud is running on
     */
    @Nonnull
    public static API getInstance() {
        return (API) ExecutorAPI.getInstance();
    }

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
                getCurrentProcessInformation().getNetworkInfo().setConnected(true);
                getCurrentProcessInformation().setProcessState(ProcessState.READY);
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
    };
}
