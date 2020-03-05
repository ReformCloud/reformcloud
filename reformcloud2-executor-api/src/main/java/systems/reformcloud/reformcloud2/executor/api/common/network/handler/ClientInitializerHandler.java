package systems.reformcloud.reformcloud2.executor.api.common.network.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty.PacketDecoder;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty.PacketEncoder;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty.serialisation.LengthDeserializer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty.serialisation.LengthSerializer;

import java.util.function.Supplier;

public final class ClientInitializerHandler extends ChannelInitializer<Channel> {

    public ClientInitializerHandler(Supplier<NetworkChannelReader> supplier, ChallengeAuthHandler challengeAuthHandler) {
        this.supplier = supplier;
        this.challengeAuthHandler = challengeAuthHandler;
    }

    private final Supplier<NetworkChannelReader> supplier;

    private final ChallengeAuthHandler challengeAuthHandler;

    @Override
    protected void initChannel(Channel channel) {
        channel.pipeline()
                .addLast("deserializer", new LengthDeserializer())
                .addLast("decoder", new PacketDecoder())
                .addLast("serializer", new LengthSerializer())
                .addLast("encoder", new PacketEncoder())
                .addLast("handler", new ChannelReaderHelper(supplier.get(), challengeAuthHandler));
    }
}
