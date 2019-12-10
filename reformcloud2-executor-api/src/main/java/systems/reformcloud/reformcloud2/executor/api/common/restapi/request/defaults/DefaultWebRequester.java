package systems.reformcloud.reformcloud2.executor.api.common.restapi.request.defaults;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionResult;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.WebRequester;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;

import javax.annotation.Nonnull;
import java.util.Collection;

public class DefaultWebRequester implements WebRequester {

    public DefaultWebRequester(ChannelHandlerContext context, String name, Collection<String> permissions) {
        this.context = context;
        this.name = name;
        this.permissions = Links.toLowerCase(permissions);
    }

    private final ChannelHandlerContext context;

    private final String name;

    private final Collection<String> permissions;

    @Nonnull
    @Override
    public Channel channel() {
        return context.channel();
    }

    @Override
    public boolean isConnected() {
        return context != null && context.channel().isOpen();
    }

    @Nonnull
    @Override
    public PermissionResult hasPermissionValue(@Nonnull String perm) {
        String matched = Links.filter(permissions, permission -> {
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

    @Nonnull
    @Override
    public String getName() {
        return name;
    }
}
