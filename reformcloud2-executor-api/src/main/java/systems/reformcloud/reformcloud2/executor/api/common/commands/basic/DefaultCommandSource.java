package systems.reformcloud.reformcloud2.executor.api.common.commands.basic;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.Permission;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionCheck;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionHolder;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionResult;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;

public final class DefaultCommandSource implements CommandSource {

  public DefaultCommandSource(Consumer<String> result,
                              CommandManager commandManager) {
    this.result = result;
    this.commandManager = commandManager;
  }

  private final Consumer<String> result;

  private final CommandManager commandManager;

  @Override
  public void sendMessage(@Nonnull String message) {
    result.accept(message);
  }

  @Override
  public void sendRawMessage(@Nonnull String message) {
    result.accept(message);
  }

  @Override
  public void sendMessages(@Nonnull String[] messages) {
    sendMessage(messages[0]);
  }

  @Override
  public void sendRawMessages(@Nonnull String[] messages) {
    sendRawMessage(messages[0]);
  }

  @Nonnull
  @Override
  public CommandManager commandManager() {
    return commandManager;
  }

  @Override
  public boolean hasPermission(@Nonnull String permission) {
    return true;
  }

  @Override
  public boolean isPermissionSet(@Nonnull String permission) {
    return true;
  }

  @Override
  public boolean hasPermission(@Nonnull Permission permission) {
    return true;
  }

  @Override
  public boolean isPermissionSet(@Nonnull Permission permission) {
    return true;
  }

  @Nonnull
  @Override
  public Collection<Permission> getEffectivePermissions() {
    return Collections.singleton(new Permission() {
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
  }

  @Override
  public void recalculatePermissions() {}

  @Nonnull
  @Override
  public PermissionCheck check() {
    return new PermissionCheck() {
      @Nonnull
      @Override
      public PermissionResult checkPermission(PermissionHolder permissionHolder,
                                              Permission permission) {
        return PermissionResult.ALLOWED;
      }

      @Nonnull
      @Override
      public PermissionResult checkPermission(PermissionHolder permissionHolder,
                                              String permission) {
        return PermissionResult.ALLOWED;
      }
    };
  }

  @Nonnull
  @Override
  public String getName() {
    return "API";
  }
}
