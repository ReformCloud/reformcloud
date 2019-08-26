package de.klaro.reformcloud2.executor.api.common.commands.basic;

import de.klaro.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import de.klaro.reformcloud2.executor.api.common.commands.Command;
import de.klaro.reformcloud2.executor.api.common.commands.permission.Permission;
import de.klaro.reformcloud2.executor.api.common.commands.permission.PermissionResult;
import de.klaro.reformcloud2.executor.api.common.commands.source.CommandSource;
import de.klaro.reformcloud2.executor.api.common.utility.list.Links;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class GlobalCommand implements Command {

    public static final String DEFAULT_DESCRIPTION = "A reformcloud2 provided command";

    public GlobalCommand(String command, String permission, String description, List<String> aliases) {
        this.mainCommand = command.toLowerCase();
        this.aliases = Links.toLowerCase(aliases);
        this.description = description;
        if (permission != null) {
            this.permissionCheck = new DefaultPermission(permission.toLowerCase(), PermissionResult.DENIED);
        } else {
            this.permissionCheck = new DefaultPermission(null, PermissionResult.ALLOWED);
        }
    }

    public GlobalCommand(String command, String description, List<String> aliases) {
        this.mainCommand = command.toLowerCase();
        this.aliases = Links.toLowerCase(aliases);
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

    @Override
    public String mainCommand() {
        return mainCommand;
    }

    @Override
    public Permission permission() {
        return permissionCheck;
    }

    @Override
    public List<String> aliases() {
        return aliases;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public AllowedCommandSources sources() {
        return AllowedCommandSources.ALL;
    }

    @Override
    public Collection<String> complete(CommandSource commandSource, String commandLine, String[] currentArg) {
        return Collections.emptyList();
    }
}
