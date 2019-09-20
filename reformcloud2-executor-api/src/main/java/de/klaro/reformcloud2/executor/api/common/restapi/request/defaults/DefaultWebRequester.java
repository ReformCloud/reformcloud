package de.klaro.reformcloud2.executor.api.common.restapi.request.defaults;

import de.klaro.reformcloud2.executor.api.common.commands.permission.PermissionResult;
import de.klaro.reformcloud2.executor.api.common.restapi.request.WebRequester;
import de.klaro.reformcloud2.executor.api.common.utility.list.Links;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Collection;
import java.util.function.Consumer;

public class DefaultWebRequester implements WebRequester {

    public DefaultWebRequester(ChannelHandlerContext context, String name, Collection<String> permissions) {
        this.context = context;
        this.name = name;
        this.permissions = Links.toLowerCase(permissions);
    }

    private final ChannelHandlerContext context;

    private final String name;

    private final Collection<String> permissions;

    @Override
    public Channel channel() {
        return context.channel();
    }

    @Override
    public void send(String message) {
        send(new TextWebSocketFrame(message));
    }

    @Override
    public void send(TextWebSocketFrame webSocketFrame) {
        context.channel().writeAndFlush(webSocketFrame);
    }

    @Override
    public void sendAnd(String message, Consumer<ChannelFuture> then) {
        sendAnd(new TextWebSocketFrame(message), then);
    }

    @Override
    public void sendAnd(TextWebSocketFrame webSocketFrame, Consumer<ChannelFuture> then) {
        context.channel().writeAndFlush(webSocketFrame).addListener((ChannelFutureListener) then::accept);
    }

    @Override
    public boolean isConnected() {
        return context != null && context.channel().isOpen();
    }

    @Override
    public PermissionResult hasPermissionValue(String perm) {
        String matched = Links.filter(permissions, permission -> {
            if (permission.startsWith("-")) {
                permission = permission.replaceFirst("-", "");
            }

            return perm.toLowerCase().equals(permission);
        });
        return matched == null ? PermissionResult.NOT_SET : matched.startsWith("-") ? PermissionResult.DENIED : PermissionResult.ALLOWED;
    }

    @Override
    public String getName() {
        return name;
    }
}
