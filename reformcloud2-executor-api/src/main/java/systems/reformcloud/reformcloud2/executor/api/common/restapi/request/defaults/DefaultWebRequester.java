package systems.reformcloud.reformcloud2.executor.api.common.restapi.request.defaults;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionResult;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.WebRequester;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;

import java.util.Collection;

public class DefaultWebRequester implements WebRequester {

    public DefaultWebRequester(ChannelHandlerContext context, String name, Collection<String> permissions) {
        this.context = context;
        this.name = name;
        this.permissions = Streams.toLowerCase(permissions);
    }

    private final ChannelHandlerContext context;

    private final String name;

    private final Collection<String> permissions;

    @NotNull
    @Override
    public Channel channel() {
        return context.channel();
    }

    @Override
    public boolean isConnected() {
        return context != null && context.channel().isOpen();
    }

    @NotNull
    @Override
    public PermissionResult hasPermissionValue(@NotNull String perm) {
        String matched = Streams.filter(permissions, permission -> {
            if (permission.equals("*")) {
                return true;
            }

            if (permission.startsWith("-")) {
                permission = permission.replaceFirst("-", "");
            }

            return perm.toLowerCase().equals(permission);
        });
        return matched == null ? PermissionResult.NOT_SET : matched.startsWith("-") ? PermissionResult.DENIED : PermissionResult.ALLOWED;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }
}
