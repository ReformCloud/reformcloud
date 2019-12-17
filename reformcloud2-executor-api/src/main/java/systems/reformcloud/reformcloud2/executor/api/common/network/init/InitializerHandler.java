package systems.reformcloud.reformcloud2.executor.api.common.network.init;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public abstract class InitializerHandler extends ChannelInitializer<Channel> {

    public InitializerHandler(@Nonnull PacketHandler packetHandler, @Nonnull Consumer<PacketSender> consumer) {
        this.packetHandler = packetHandler;
        this.senderConsumer = consumer;
    }

    private final PacketHandler packetHandler;

    private final Consumer<PacketSender> senderConsumer;

    /**
     * @see NetworkUtil#newReader(PacketHandler, Consumer)
     *
     * @return A new {@link NetworkChannelReader} with the given values
     */
    @Nonnull
    protected final NetworkChannelReader newHandler() {
        return NetworkUtil.newReader(packetHandler, senderConsumer);
    }
}
