package de.klaro.reformcloud2.executor.api.common.network.handler;

import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.init.InitializerHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.serialisation.LengthDeserializer;
import de.klaro.reformcloud2.executor.api.common.network.packet.serialisation.LengthSerializer;
import de.klaro.reformcloud2.executor.api.common.network.packet.util.PacketDecoder;
import de.klaro.reformcloud2.executor.api.common.network.packet.util.PacketEncoder;
import de.klaro.reformcloud2.executor.api.common.utility.function.DoubleFunction;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class ServerInitializerHandler extends InitializerHandler {

    public ServerInitializerHandler(PacketHandler packetHandler, Consumer<PacketSender> consumer, DoubleFunction<Packet, String, Boolean> function,
                                    BiFunction<String, ChannelHandlerContext, PacketSender> onAuthSuccess, Consumer<ChannelHandlerContext> onAuthFailure) {
        super(packetHandler, consumer);
        this.function = function;
        this.onSuccess = onAuthSuccess;
        this.onAuthFailure = onAuthFailure;
    }

    private final DoubleFunction<Packet, String, Boolean> function;

    private final BiFunction<String, ChannelHandlerContext, PacketSender> onSuccess;

    private final Consumer<ChannelHandlerContext> onAuthFailure;

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                .addLast("deserializer", new LengthDeserializer())
                .addLast("decoder", new PacketDecoder())
                .addLast("serializer", new LengthSerializer())
                .addLast("encoder", new PacketEncoder())
                .addLast("handler", new ChannelReaderHelper(newHandler(), function, onSuccess, onAuthFailure));
    }
}
