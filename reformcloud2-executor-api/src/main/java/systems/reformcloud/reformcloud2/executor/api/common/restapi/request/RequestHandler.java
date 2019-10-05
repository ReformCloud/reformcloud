package systems.reformcloud.reformcloud2.executor.api.common.restapi.request;

import java.util.function.Consumer;

public interface RequestHandler {

    HandlingRequestType handlingRequestType();

    void handleRequest(WebRequester webRequester, Object in, Consumer<Object> response);
}
