package systems.reformcloud.reformcloud2.executor.api.common.commands.permission;

public enum PermissionResult {

    /**
     * The permission is set and the user is allowed to use the permission
     */
    ALLOWED(true),

    /**
     * The permission is set and the user is not allowed to use the permission
     */
    DENIED(false),

    /**
     * The permission is not set and the user is not allowed to use the permission
     */
    NOT_SET(false);

    PermissionResult(boolean javaValue) {
        this.javaValue = javaValue;
    }

    private final boolean javaValue;

    public boolean isAllowed() {
        return javaValue;
    }
}
