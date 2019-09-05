package de.klaro.reformcloud2.executor.api.common.network.handler;

import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import de.klaro.reformcloud2.executor.api.common.network.client.NetworkClient;
import de.klaro.reformcloud2.executor.api.common.network.init.InitializerHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.network.packet.serialisation.LengthDeserializer;
import de.klaro.reformcloud2.executor.api.common.network.packet.serialisation.LengthSerializer;
import de.klaro.reformcloud2.executor.api.common.network.packet.util.PacketDecoder;
import de.klaro.reformcloud2.executor.api.common.network.packet.util.PacketEncoder;
import de.klaro.reformcloud2.executor.api.common.utility.function.Double;
import de.klaro.reformcloud2.executor.api.common.utility.function.DoubleFunction;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.function.Consumer;

public final class ClientInitializerHandler extends InitializerHandler {

    private static final DoubleFunction<Packet, String, Boolean> EMPTY_FUNCTION = new DoubleFunction<Packet, String, Boolean>() {
        @Override
        public Double<String, Boolean> apply(Packet packet) {
            return new Double<>("Controller", true);
        }
    };

    private final Consumer<ChannelHandlerContext> DEFAULT_FAIL_HANDLER = new Consumer<ChannelHandlerContext>() {
        @Override
        public void accept(ChannelHandlerContext context) {
            context.channel().close();
            networkClient.disconnect();
        }
    };

    public ClientInitializerHandler(NetworkChannelReader channelReader, NetworkClient networkClient) {
        super(null, null);
        this.networkClient = networkClient;
        this.channelReader = channelReader;
    }

    private final NetworkClient networkClient;

    private final NetworkChannelReader channelReader;

    @Override
    protected void initChannel(Channel channel) {
        channel.pipeline()
                .addLast("deserializer", new LengthDeserializer())
                .addLast("decoder", new PacketDecoder())
                .addLast("serializer", new LengthSerializer())
                .addLast("encoder", new PacketEncoder())
                .addLast("handler", new ChannelReaderHelper(
                        channelReader,
                        EMPTY_FUNCTION,
                        NetworkUtil.DEFAULT_SUCCESS_HANDLER,
                        DEFAULT_FAIL_HANDLER
                ));
    }
}
