package systems.reformcloud.reformcloud2.executor.api.common.restapi.auth;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.Configurable;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.WebRequester;
import systems.reformcloud.reformcloud2.executor.api.common.utility.function.Double;

import javax.annotation.Nonnull;

public interface Auth {

    /**
     * Tries to handle the auth with an json configurable
     *
     * @param configurable The configurable which got parsed by the incoming http request
     * @param channelHandlerContext The channel context of the incoming request
     * @return A double with the key value {@code true} if the auth was successful and as value the {@link WebRequester} if the auth was successful
     */
    @Nonnull
    Double<Boolean, WebRequester> handleAuth(@Nonnull Configurable<JsonConfiguration> configurable, @Nonnull ChannelHandlerContext channelHandlerContext);
}
