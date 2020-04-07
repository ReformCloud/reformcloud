package systems.reformcloud.reformcloud2.executor.api.common.network.channel;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.ReNameable;

public interface DirectIdentifiableChannel extends ReNameable {

    /**
     * @return The channel context of the current channel
     */
    @NotNull
    ChannelHandlerContext getChannelContext();
}
