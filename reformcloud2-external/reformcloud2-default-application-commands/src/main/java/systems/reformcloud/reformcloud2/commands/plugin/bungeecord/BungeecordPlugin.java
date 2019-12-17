package systems.reformcloud.reformcloud2.commands.plugin.bungeecord;

import javax.annotation.Nonnull;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import systems.reformcloud.reformcloud2.commands.config.CommandsConfig;
import systems.reformcloud.reformcloud2.commands.plugin.CommandConfigHandler;
import systems.reformcloud.reformcloud2.commands.plugin.bungeecord.commands.CommandLeave;
import systems.reformcloud.reformcloud2.commands.plugin.bungeecord.commands.CommandReformCloud;
import systems.reformcloud.reformcloud2.commands.plugin.packet.in.PacketInRegisterCommandsConfig;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;

public class BungeecordPlugin extends Plugin {

  private static BungeecordPlugin instance;

  @Override
  public void onEnable() {
    instance = this;
    CommandConfigHandler.setInstance(new ConfigHandler());
    ExecutorAPI.getInstance().getPacketHandler().registerHandler(
        new PacketInRegisterCommandsConfig());
  }

  @Override
  public void onDisable() {
    CommandConfigHandler.getInstance().unregisterAllCommands();
    ExecutorAPI.getInstance().getPacketHandler().unregisterNetworkHandlers(
        NetworkUtil.EXTERNAL_BUS + 1);
  }

  private static class ConfigHandler extends CommandConfigHandler {

    private CommandLeave leave;

    private CommandReformCloud reformCloud;

    @Override
    public void
    handleCommandConfigRelease(@Nonnull CommandsConfig commandsConfig) {
      unregisterAllCommands();
      if (commandsConfig.isLeaveCommandEnabled() &&
          commandsConfig.getLeaveCommands().size() > 0) {
        String name = commandsConfig.getLeaveCommands().get(0);
        if (commandsConfig.getLeaveCommands().size() - 1 > 0) {
          commandsConfig.getLeaveCommands().remove(name);
        }

        this.leave = new CommandLeave(name, commandsConfig.getLeaveCommands());
        ProxyServer.getInstance().getPluginManager().registerCommand(
            instance, this.leave);
      }

      if (commandsConfig.isReformCloudCommandEnabled() &&
          commandsConfig.getReformCloudCommands().size() > 0) {
        String name = commandsConfig.getReformCloudCommands().get(0);
        if (commandsConfig.getReformCloudCommands().size() - 1 > 0) {
          commandsConfig.getReformCloudCommands().remove(name);
        }

        this.reformCloud = new CommandReformCloud(
            name, commandsConfig.getReformCloudCommands());
        ProxyServer.getInstance().getPluginManager().registerCommand(
            instance, this.reformCloud);
      }
    }

    @Override
    public void unregisterAllCommands() {
      if (this.leave != null) {
        ProxyServer.getInstance().getPluginManager().unregisterCommand(leave);
        this.leave = null;
      }

      if (this.reformCloud == null) {
        ProxyServer.getInstance().getPluginManager().unregisterCommand(
            reformCloud);
        this.reformCloud = null;
      }
    }
  }
}
