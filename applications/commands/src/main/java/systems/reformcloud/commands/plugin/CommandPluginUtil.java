package systems.reformcloud.commands.plugin;

import systems.refomcloud.embedded.Embedded;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.commands.application.packet.PacketGetCommandsConfig;
import systems.reformcloud.commands.application.packet.PacketGetCommandsConfigResult;
import systems.reformcloud.commands.plugin.packet.PacketReleaseCommandsConfig;
import systems.reformcloud.network.PacketIds;
import systems.reformcloud.network.packet.PacketProvider;

public final class CommandPluginUtil {

  private CommandPluginUtil() {
    throw new UnsupportedOperationException();
  }

  public static void init() {
    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(PacketProvider.class).registerPacket(PacketGetCommandsConfigResult.class);
    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(PacketProvider.class).registerPacket(PacketReleaseCommandsConfig.class);

    Embedded.getInstance().sendSyncQuery(new PacketGetCommandsConfig()).ifPresent(e -> {
      if (e instanceof PacketGetCommandsConfigResult) {
        CommandConfigHandler.getInstance().handleCommandConfigRelease(((PacketGetCommandsConfigResult) e).getCommandsConfig());
      }
    });
  }

  public static void close() {
    CommandConfigHandler.getInstance().unregisterAllCommands();
    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(PacketProvider.class).unregisterPacket(PacketIds.RESERVED_EXTRA_BUS + 3);
    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(PacketProvider.class).unregisterPacket(PacketIds.RESERVED_EXTRA_BUS + 2);
  }
}
