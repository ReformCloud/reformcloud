package systems.reformcloud.reformcloud2.web.tokens;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionResult;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.defaults.DefaultWebRequester;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class SetupWebRequester extends DefaultWebRequester {

    public SetupWebRequester(ChannelHandlerContext context, String name) {
        super(context, name, new ArrayList<>());
    }

    @Override
    public PermissionResult hasPermissionValue(@Nonnull String perm) {
        return perm.equals("setup.allow") ? PermissionResult.ALLOWED : PermissionResult.DENIED;
    }
}
