package systems.reformcloud.reformcloud2.executor.api.common.restapi.auth;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.Configurable;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.WebRequester;
import systems.reformcloud.reformcloud2.executor.api.common.utility.function.Double;

public interface Auth {

    Double<Boolean, WebRequester> handleAuth(Configurable configurable, ChannelHandlerContext channelHandlerContext);
}
