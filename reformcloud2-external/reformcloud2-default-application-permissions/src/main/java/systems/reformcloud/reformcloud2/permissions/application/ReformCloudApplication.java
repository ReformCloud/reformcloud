package systems.reformcloud.reformcloud2.permissions.application;

import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.application.command.CommandPerms;
import systems.reformcloud.reformcloud2.permissions.packets.PacketHelper;

public class ReformCloudApplication extends Application {

  @Override
  public void onEnable() {
    PacketHelper.addControllerPackets();
    PermissionAPI.handshake();

    getCommandManager().register(new CommandPerms());
  }

  @Override
  public void onUninstall() {
    PacketHelper.unregisterControllerPackets();
  }

  private CommandManager getCommandManager() {
    return ExecutorAPI.getInstance().getType().equals(ExecutorType.CONTROLLER)
        ? ControllerExecutor.getInstance().getCommandManager()
        : NodeExecutor.getInstance().getCommandManager();
  }
}
