package systems.reformcloud.reformcloud2.permissions;

import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.permissions.packets.PacketHelper;
import systems.reformcloud.reformcloud2.permissions.util.PermissionUtil;
import systems.reformcloud.reformcloud2.permissions.util.basic.DefaultPermissionUtil;

public class PermissionAPI {

    public static final PermissionAPI INSTANCE = new PermissionAPI();

    private PermissionAPI() {
        this.permissionUtil = DefaultPermissionUtil.doLoad();
        if (ExecutorAPI.getInstance().getType().equals(ExecutorType.API)) {
            PacketHelper.addAPIPackets();
        }
    }

    private final PermissionUtil permissionUtil;

    public PermissionUtil getPermissionUtil() {
        return permissionUtil;
    }
}
