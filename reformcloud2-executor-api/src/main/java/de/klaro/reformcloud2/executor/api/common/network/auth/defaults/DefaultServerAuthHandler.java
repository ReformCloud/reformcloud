package de.klaro.reformcloud2.executor.api.common.network.auth.defaults;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.auth.ServerAuthHandler;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.defaults.DefaultPacketSender;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import de.klaro.reformcloud2.executor.api.common.utility.function.DoubleFunction;
import io.netty.channel.ChannelHandlerContext;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class DefaultServerAuthHandler implements ServerAuthHandler {

    public DefaultServerAuthHandler(PacketHandler packetHandler, Consumer<PacketSender> consumer, DoubleFunction<Packet, String, Boolean> authHandler) {
        this.authHandler = authHandler;
        this.packetHandler = packetHandler;
        this.packetSenderConsumer = consumer;
    }

    private final PacketHandler packetHandler;

    private final Consumer<PacketSender> packetSenderConsumer;

    private final DoubleFunction<Packet, String, Boolean> authHandler;

    @Override
    public PacketHandler packetHandler() {
        return packetHandler;
    }

    @Override
    public Consumer<PacketSender> onDisconnect() {
        return packetSenderConsumer;
    }

    @Override
    public DoubleFunction<Packet, String, Boolean> function() {
        return authHandler;
    }

    @Override
    public BiFunction<String, ChannelHandlerContext, PacketSender> onSuccess() {
        return new BiFunction<String, ChannelHandlerContext, PacketSender>() {
            @Override
            public PacketSender apply(String s, ChannelHandlerContext context) {
                context.channel().writeAndFlush(new DefaultPacket(-511, new JsonConfiguration().add("access", true)));
                PacketSender sender = new DefaultPacketSender(context.channel());
                sender.setName(s);
                return sender;
            }
        };
    }

    @Override
    public Consumer<ChannelHandlerContext> onAuthFailure() {
        return NetworkUtil.DEFAULT_AUTH_FAILURE_HANDLER;
    }
}
