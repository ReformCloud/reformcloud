package de.klaro.reformcloud2.executor.api.common.commands.permission;

public enum PermissionResult {

    ALLOWED(true),

    DENIED(false),

    NOT_SET(false);

    PermissionResult(boolean javaValue) {
        this.javaValue = javaValue;
    }

    private final boolean javaValue;

    public boolean isAllowed() {
        return javaValue;
    }
}
