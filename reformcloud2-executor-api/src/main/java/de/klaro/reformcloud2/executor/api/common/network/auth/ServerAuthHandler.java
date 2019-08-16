package de.klaro.reformcloud2.executor.api.common.network.auth;

import de.klaro.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.utility.function.DoubleFunction;
import io.netty.channel.ChannelHandlerContext;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface ServerAuthHandler {

    NetworkChannelReader channelReader();

    DoubleFunction<Packet, String, Boolean> function();

    BiFunction<String, ChannelHandlerContext, PacketSender> onSuccess();

    Consumer<ChannelHandlerContext> onAuthFailure();
}
