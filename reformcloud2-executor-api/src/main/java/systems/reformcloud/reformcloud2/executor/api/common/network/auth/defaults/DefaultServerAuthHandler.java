package systems.reformcloud.reformcloud2.executor.api.common.network.auth.defaults;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.ServerAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.defaults.DefaultPacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.utility.function.DoubleFunction;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class DefaultServerAuthHandler implements ServerAuthHandler {

    public DefaultServerAuthHandler(PacketHandler packetHandler, Consumer<PacketSender> consumer, DoubleFunction<Packet, String, Boolean> authHandler) {
        this.authHandler = authHandler;
        this.packetHandler = packetHandler;
        this.packetSenderConsumer = consumer;
    }

    private final PacketHandler packetHandler;

    private final Consumer<PacketSender> packetSenderConsumer;

    private final DoubleFunction<Packet, String, Boolean> authHandler;

    @Nonnull
    @Override
    public PacketHandler packetHandler() {
        return packetHandler;
    }

    @Nonnull
    @Override
    public Consumer<PacketSender> onDisconnect() {
        return packetSenderConsumer;
    }

    @Nonnull
    @Override
    public DoubleFunction<Packet, String, Boolean> function() {
        return authHandler;
    }

    @Nonnull
    @Override
    public BiFunction<String, ChannelHandlerContext, PacketSender> onSuccess() {
        return (s, context) -> {
            context.channel().writeAndFlush(new JsonPacket(-511, new JsonConfiguration().add("access", true)));
            PacketSender sender = new DefaultPacketSender(context);
            sender.setName(s);
            return sender;
        };
    }

    @Nonnull
    @Override
    public Consumer<ChannelHandlerContext> onAuthFailure() {
        return NetworkUtil.DEFAULT_AUTH_FAILURE_HANDLER;
    }
}
