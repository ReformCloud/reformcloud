package systems.reformcloud.reformcloud2.executor.api.common.network.auth;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.utility.function.DoubleFunction;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface ServerAuthHandler {

    PacketHandler packetHandler();

    Consumer<PacketSender> onDisconnect();

    DoubleFunction<Packet, String, Boolean> function();

    BiFunction<String, ChannelHandlerContext, PacketSender> onSuccess();

    Consumer<ChannelHandlerContext> onAuthFailure();
}
