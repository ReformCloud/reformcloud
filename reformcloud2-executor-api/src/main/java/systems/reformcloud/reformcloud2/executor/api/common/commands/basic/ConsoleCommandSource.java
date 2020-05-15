/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.api.common.commands.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.Permission;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionCheck;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionHolder;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionResult;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.logger.coloured.Colours;

import java.util.Collection;
import java.util.Collections;

public class ConsoleCommandSource implements CommandSource {

    private static final PermissionCheck CONSOLE_COMMAND_CHECK = new ConsoleCommandCheck();

    private static final Collection<Permission> PERMISSIONS = Collections.singletonList(new Permission() {
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
    private final CommandManager commandManager;

    public ConsoleCommandSource(CommandManager commandManager) {
        this.commandManager = commandManager;
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
        if (hasPermission(permission.permission())) {
            return true;
        }

        return permission.defaultResult().isAllowed();
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission permission) {
        if (isPermissionSet(permission.permission())) {
            return true;
        }

        return permission.defaultResult().isAllowed();
    }

    @NotNull
    @Override
    public Collection<Permission> getEffectivePermissions() {
        return PERMISSIONS;
    }

    @Override
    public void recalculatePermissions() {
    }

    @NotNull
    @Override
    public PermissionCheck check() {
        return CONSOLE_COMMAND_CHECK;
    }

    @NotNull
    @Override
    public String getName() {
        return "ReformCloudConsole";
    }

    @Override
    public void sendMessage(@NotNull String message) {
        System.out.println(message);
    }

    @Override
    public void sendRawMessage(@NotNull String message) {
        System.out.println(Colours.stripColor(message));
    }

    @Override
    public void sendMessages(@NotNull String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    @Override
    public void sendRawMessages(@NotNull String[] messages) {
        for (String message : messages) {
            sendRawMessage(message);
        }
    }

    @NotNull
    @Override
    public CommandManager commandManager() {
        return commandManager;
    }

    private static class ConsoleCommandCheck implements PermissionCheck {

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
    }
}
