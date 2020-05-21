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
import systems.reformcloud.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.Permission;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionResult;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;

import java.util.*;

public abstract class GlobalCommand implements Command {

    public static final String DEFAULT_DESCRIPTION = "A reformcloud2 provided command";
    private final List<String> aliases;
    private final String mainCommand;
    private final String description;
    private final Permission permissionCheck;

    public GlobalCommand(String command, String permission, String description, String... aliases) {
        this(command, permission, description, Arrays.asList(aliases));
    }

    public GlobalCommand(String command, String permission, String description, List<String> aliases) {
        this.mainCommand = command.toLowerCase();
        this.aliases = Streams.toLowerCase(aliases);
        this.description = description;
        if (permission != null) {
            this.permissionCheck = new DefaultPermission(permission.toLowerCase(), PermissionResult.DENIED);
        } else {
            this.permissionCheck = new DefaultPermission(null, PermissionResult.ALLOWED);
        }
    }

    public GlobalCommand(String command, String description, List<String> aliases) {
        this.mainCommand = command.toLowerCase();
        this.aliases = Streams.toLowerCase(aliases);
        this.description = description;
        this.permissionCheck = new DefaultPermission(null, PermissionResult.ALLOWED);
    }

    public GlobalCommand(String command, String description) {
        this.mainCommand = command.toLowerCase();
        this.aliases = new ArrayList<>();
        this.description = description;
        this.permissionCheck = new DefaultPermission(null, PermissionResult.ALLOWED);
    }

    public GlobalCommand(String command) {
        this.mainCommand = command.toLowerCase();
        this.aliases = new ArrayList<>();
        this.description = DEFAULT_DESCRIPTION;
        this.permissionCheck = new DefaultPermission(null, PermissionResult.ALLOWED);
    }

    @NotNull
    @Override
    public String mainCommand() {
        return this.mainCommand;
    }

    @Override
    public Permission permission() {
        return this.permissionCheck;
    }

    @NotNull
    @Override
    public List<String> aliases() {
        return this.aliases;
    }

    @NotNull
    @Override
    public String description() {
        return this.description;
    }

    @NotNull
    @Override
    public AllowedCommandSources sources() {
        return AllowedCommandSources.ALL;
    }

    @Override
    public void describeCommandToSender(@NotNull CommandSource source) {
        source.sendMessage(this.description);
    }

    @NotNull
    @Override
    public Collection<String> complete(@NotNull CommandSource commandSource, @NotNull String commandLine, @NotNull String[] currentArg) {
        return Collections.emptyList();
    }
}
