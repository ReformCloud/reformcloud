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

    private final List<String> aliases;

    private final String mainCommand;

    private final String description;

    private final Permission permissionCheck;

    @NotNull
    @Override
    public String mainCommand() {
        return mainCommand;
    }

    @Override
    public Permission permission() {
        return permissionCheck;
    }

    @NotNull
    @Override
    public List<String> aliases() {
        return aliases;
    }

    @NotNull
    @Override
    public String description() {
        return description;
    }

    @NotNull
    @Override
    public AllowedCommandSources sources() {
        return AllowedCommandSources.ALL;
    }

    @Override
    public void describeCommandToSender(@NotNull CommandSource source) {
        source.sendMessage(description);
    }

    @NotNull
    @Override
    public Collection<String> complete(@NotNull CommandSource commandSource, @NotNull String commandLine, @NotNull String[] currentArg) {
        return Collections.emptyList();
    }
}
