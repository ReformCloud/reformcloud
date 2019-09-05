package de.klaro.reformcloud2.executor.api.common.network.init;

import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

import java.util.function.Consumer;

public abstract class InitializerHandler extends ChannelInitializer<Channel> {

    public InitializerHandler(PacketHandler packetHandler, Consumer<PacketSender> consumer) {
        this.packetHandler = packetHandler;
        this.senderConsumer = consumer;
    }

    private final PacketHandler packetHandler;

    private final Consumer<PacketSender> senderConsumer;

    protected final NetworkChannelReader newHandler() {
        return NetworkUtil.newReader(packetHandler, senderConsumer);
    }
}
