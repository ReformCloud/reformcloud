package de.klaro.reformcloud2.executor.api.common.network.init;

import de.klaro.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public abstract class InitializerHandler extends ChannelInitializer<Channel> {

    public InitializerHandler(NetworkChannelReader channelReader) {
        this.networkChannelReader = channelReader;
    }

    protected final NetworkChannelReader networkChannelReader;
}
