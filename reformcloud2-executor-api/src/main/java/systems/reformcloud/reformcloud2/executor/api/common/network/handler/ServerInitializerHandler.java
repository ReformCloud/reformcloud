package systems.reformcloud.reformcloud2.executor.api.common.network.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty.PacketDecoder;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty.PacketEncoder;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty.serialisation.LengthDeserializer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty.serialisation.LengthSerializer;

import java.util.function.Supplier;

public final class ServerInitializerHandler extends ChannelInitializer<Channel> {

    public ServerInitializerHandler(Supplier<NetworkChannelReader> reader, ChallengeAuthHandler handler) {
        this.reader = reader;
        this.authHandler = handler;
    }

    private final ChallengeAuthHandler authHandler;

    private final Supplier<NetworkChannelReader> reader;

    @Override
    protected void initChannel(Channel channel) {
        channel.pipeline()
                .addLast("deserializer", new LengthDeserializer())
                .addLast("decoder", new PacketDecoder())
                .addLast("serializer", new LengthSerializer())
                .addLast("encoder", new PacketEncoder())
                .addLast("handler", new ChannelReaderHelper(this.reader.get(), this.authHandler));
    }
}
