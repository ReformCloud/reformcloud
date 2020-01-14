package systems.reformcloud.reformcloud2.executor.api.common.network.channel;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.ReNameable;

import javax.annotation.Nonnull;

public interface DirectIdentifiableChannel extends ReNameable {

    /**
     * @return The channel context of the current channel
     */
    @Nonnull
    ChannelHandlerContext getChannelContext();
}
