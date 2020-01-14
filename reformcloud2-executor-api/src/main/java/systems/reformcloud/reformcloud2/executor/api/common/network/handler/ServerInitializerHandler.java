package systems.reformcloud.reformcloud2.executor.api.common.network.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.init.InitializerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty.PacketDecoder;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty.PacketEncoder;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty.serialisation.LengthDeserializer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty.serialisation.LengthSerializer;
import systems.reformcloud.reformcloud2.executor.api.common.utility.function.DoubleFunction;

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
    protected void initChannel(Channel channel) {
        channel.pipeline()
                .addLast("deserializer", new LengthDeserializer())
                .addLast("decoder", new PacketDecoder())
                .addLast("serializer", new LengthSerializer())
                .addLast("encoder", new PacketEncoder())
                .addLast("handler", new ChannelReaderHelper(newHandler(), function, onSuccess, onAuthFailure));
    }
}
