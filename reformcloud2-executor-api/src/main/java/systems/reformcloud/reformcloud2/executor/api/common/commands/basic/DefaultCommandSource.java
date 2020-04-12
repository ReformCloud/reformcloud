package systems.reformcloud.reformcloud2.executor.api.common.commands.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.Permission;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionCheck;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionHolder;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionResult;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

public final class DefaultCommandSource implements CommandSource {

    public DefaultCommandSource(Consumer<String> result, CommandManager commandManager) {
        this.result = result;
        this.commandManager = commandManager;
    }

    private final Consumer<String> result;

    private final CommandManager commandManager;

    @Override
    public void sendMessage(@NotNull String message) {
        result.accept(message);
    }

    @Override
    public void sendRawMessage(@NotNull String message) {
        result.accept(message);
    }

    @Override
    public void sendMessages(@NotNull String[] messages) {
        sendMessage(messages[0]);
    }

    @Override
    public void sendRawMessages(@NotNull String[] messages) {
        sendRawMessage(messages[0]);
    }

    @NotNull
    @Override
    public CommandManager commandManager() {
        return commandManager;
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return true;
    }

    @Override
    public boolean isPermissionSet(@NotNull String permission) {
        return true;
    }

    @Override
    public boolean hasPermission(@NotNull Permission permission) {
        return true;
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission permission) {
        return true;
    }

    @NotNull
    @Override
    public Collection<Permission> getEffectivePermissions() {
        return Collections.singleton(new Permission() {
            @NotNull
            @Override
            public String permission() {
                return "*";
            }

            @NotNull
            @Override
            public PermissionResult defaultResult() {
                return PermissionResult.ALLOWED;
            }
        });
    }

    @Override
    public void recalculatePermissions() {
    }

    @NotNull
    @Override
    public PermissionCheck check() {
        return new PermissionCheck() {
            @NotNull
            @Override
            public PermissionResult checkPermission(PermissionHolder permissionHolder, Permission permission) {
                return PermissionResult.ALLOWED;
            }

            @NotNull
            @Override
            public PermissionResult checkPermission(PermissionHolder permissionHolder, String permission) {
                return PermissionResult.ALLOWED;
            }
        };
    }

    @NotNull
    @Override
    public String getName() {
        return "API";
    }
}
