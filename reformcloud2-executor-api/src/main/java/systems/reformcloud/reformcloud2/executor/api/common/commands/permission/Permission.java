package systems.reformcloud.reformcloud2.executor.api.common.commands.permission;

import javax.annotation.Nonnull;

public interface Permission {

    /**
     * @return The plain permission
     */
    @Nonnull
    String permission();

    /**
     * @return The default {@link PermissionResult}
     */
    @Nonnull
    PermissionResult defaultResult();
}
