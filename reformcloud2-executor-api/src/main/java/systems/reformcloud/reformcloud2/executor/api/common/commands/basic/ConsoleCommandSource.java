package systems.reformcloud.reformcloud2.executor.api.common.commands.basic;

import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.Permission;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionCheck;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionHolder;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionResult;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.logger.coloured.Colours;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

public final class ConsoleCommandSource implements CommandSource {

    private static final PermissionCheck CONSOLE_COMMAND_CHECK = new ConsoleCommandCheck();

    private static final Collection<Permission> PERMISSIONS = Collections.singletonList(new Permission() {
        @Nonnull
        @Override
        public String permission() {
            return "*";
        }

        @Nonnull
        @Override
        public PermissionResult defaultResult() {
            return PermissionResult.ALLOWED;
        }
    });

    public ConsoleCommandSource(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    private final CommandManager commandManager;

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public boolean isPermissionSet(String permission) {
        return true;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        if (hasPermission(permission.permission())) {
            return true;
        }

        return permission.defaultResult().isAllowed();
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        if (isPermissionSet(permission.permission())) {
            return true;
        }

        return permission.defaultResult().isAllowed();
    }

    @Nonnull
    @Override
    public Collection<Permission> getEffectivePermissions() {
        return PERMISSIONS;
    }

    @Override
    public void recalculatePermissions() {
    }

    @Nonnull
    @Override
    public PermissionCheck check() {
        return CONSOLE_COMMAND_CHECK;
    }

    @Nonnull
    @Override
    public String getName() {
        return "ReformCloudConsole";
    }

    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void sendRawMessage(String message) {
        System.out.println(Colours.stripColor(message));
    }

    @Override
    public void sendMessages(String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    @Override
    public void sendRawMessages(String[] messages) {
        for (String message : messages) {
            sendRawMessage(message);
        }
    }

    @Nonnull
    @Override
    public CommandManager commandManager() {
        return commandManager;
    }

    private static class ConsoleCommandCheck implements PermissionCheck {

        @Nonnull
        @Override
        public PermissionResult checkPermission(PermissionHolder permissionHolder, Permission permission) {
            return PermissionResult.ALLOWED;
        }

        @Nonnull
        @Override
        public PermissionResult checkPermission(PermissionHolder permissionHolder, String permission) {
            return PermissionResult.ALLOWED;
        }
    }
}
