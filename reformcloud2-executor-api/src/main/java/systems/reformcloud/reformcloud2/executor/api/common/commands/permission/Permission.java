package systems.reformcloud.reformcloud2.executor.api.common.commands.permission;

public interface Permission {

    /**
     * @return The plain permission
     */
    String permission();

    /**
     * @return The default {@link PermissionResult}
     */
    PermissionResult defaultResult();
}
