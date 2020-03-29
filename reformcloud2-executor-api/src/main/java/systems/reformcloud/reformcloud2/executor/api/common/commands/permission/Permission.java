package systems.reformcloud.reformcloud2.executor.api.common.commands.permission;

import org.jetbrains.annotations.NotNull;

public interface Permission {

    /**
     * @return The plain permission
     */
    @NotNull
    String permission();

    /**
     * @return The default {@link PermissionResult}
     */
    @NotNull
    PermissionResult defaultResult();
}
