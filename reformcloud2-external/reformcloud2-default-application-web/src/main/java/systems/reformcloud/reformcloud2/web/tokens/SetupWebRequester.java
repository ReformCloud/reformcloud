package systems.reformcloud.reformcloud2.web.tokens;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionResult;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.defaults.DefaultWebRequester;

import java.util.ArrayList;

public class SetupWebRequester extends DefaultWebRequester {

    public SetupWebRequester(ChannelHandlerContext context, String name) {
        super(context, name, new ArrayList<>());
    }

    @NotNull
    @Override
    public PermissionResult hasPermissionValue(@NotNull String perm) {
        return perm.equals("setup.allow") ? PermissionResult.ALLOWED : PermissionResult.DENIED;
    }
}
