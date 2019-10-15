package systems.reformcloud.reformcloud2.executor.api.common.restapi.request;

import io.netty.channel.Channel;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionResult;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

public interface WebRequester extends Nameable {

    Channel channel();

    boolean isConnected();

    PermissionResult hasPermissionValue(String perm);

    default boolean hasPermission(String perm) {
        return hasPermissionValue(perm).isAllowed();
    }
}
