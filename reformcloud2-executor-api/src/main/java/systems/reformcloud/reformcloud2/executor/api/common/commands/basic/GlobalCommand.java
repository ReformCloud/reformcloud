package systems.reformcloud.reformcloud2.executor.api.common.commands.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.Permission;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionResult;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;

public abstract class GlobalCommand implements Command {

  public static final String DEFAULT_DESCRIPTION =
      "A reformcloud2 provided command";

  public GlobalCommand(String command, String permission, String description,
                       List<String> aliases) {
    this.mainCommand = command.toLowerCase();
    this.aliases = Links.toLowerCase(aliases);
    this.description = description;
    if (permission != null) {
      this.permissionCheck = new DefaultPermission(permission.toLowerCase(),
                                                   PermissionResult.DENIED);
    } else {
      this.permissionCheck =
          new DefaultPermission(null, PermissionResult.ALLOWED);
    }
  }

  public GlobalCommand(String command, String description,
                       List<String> aliases) {
    this.mainCommand = command.toLowerCase();
    this.aliases = Links.toLowerCase(aliases);
    this.description = description;
    this.permissionCheck =
        new DefaultPermission(null, PermissionResult.ALLOWED);
  }

  public GlobalCommand(String command, String description) {
    this.mainCommand = command.toLowerCase();
    this.aliases = new ArrayList<>();
    this.description = description;
    this.permissionCheck =
        new DefaultPermission(null, PermissionResult.ALLOWED);
  }

  public GlobalCommand(String command) {
    this.mainCommand = command.toLowerCase();
    this.aliases = new ArrayList<>();
    this.description = DEFAULT_DESCRIPTION;
    this.permissionCheck =
        new DefaultPermission(null, PermissionResult.ALLOWED);
  }

  private final List<String> aliases;

  private final String mainCommand;

  private final String description;

  private final Permission permissionCheck;

  @Nonnull
  @Override
  public String mainCommand() {
    return mainCommand;
  }

  @Override
  public Permission permission() {
    return permissionCheck;
  }

  @Nonnull
  @Override
  public List<String> aliases() {
    return aliases;
  }

  @Nonnull
  @Override
  public String description() {
    return description;
  }

  @Nonnull
  @Override
  public AllowedCommandSources sources() {
    return AllowedCommandSources.ALL;
  }

  @Nonnull
  @Override
  public Collection<String> complete(@Nonnull CommandSource commandSource,
                                     @Nonnull String commandLine,
                                     @Nonnull String[] currentArg) {
    return Collections.emptyList();
  }
}
