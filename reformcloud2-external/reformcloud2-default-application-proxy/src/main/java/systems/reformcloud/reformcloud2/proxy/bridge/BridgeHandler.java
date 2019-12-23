package systems.reformcloud.reformcloud2.proxy.bridge;

import java.util.function.BiConsumer;

public interface BridgeHandler {

    BiConsumer<String, String> initTab();
}
