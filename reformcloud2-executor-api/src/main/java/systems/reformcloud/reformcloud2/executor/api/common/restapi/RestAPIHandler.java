package systems.reformcloud.reformcloud2.executor.api.common.restapi;

import io.netty.channel.SimpleChannelInboundHandler;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;

public abstract class RestAPIHandler extends SimpleChannelInboundHandler<Object> {

    public RestAPIHandler(RequestListenerHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    protected final RequestListenerHandler requestHandler;
}
