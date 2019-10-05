package systems.reformcloud.reformcloud2.executor.api.common.restapi.request;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionResult;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.function.Consumer;

public interface WebRequester extends Nameable {

    Channel channel();

    void send(String message);

    void send(TextWebSocketFrame webSocketFrame);

    void sendAnd(String message, Consumer<ChannelFuture> then);

    void sendAnd(TextWebSocketFrame webSocketFrame, Consumer<ChannelFuture> then);

    boolean isConnected();

    PermissionResult hasPermissionValue(String perm);

    default boolean hasPermission(String perm) {
        return hasPermissionValue(perm).isAllowed();
    }
}
