package systems.reformcloud.reformcloud2.executor.api.common.restapi.request;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import java.util.function.Consumer;

public interface RequestHandler {

    String path();

    void handleRequest(WebRequester webRequester, HttpRequest in, Consumer<HttpResponse> response);
}
