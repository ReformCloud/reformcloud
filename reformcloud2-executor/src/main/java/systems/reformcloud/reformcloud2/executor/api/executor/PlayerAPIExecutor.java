package systems.reformcloud.reformcloud2.executor.api.executor;

import java.util.UUID;
import javax.annotation.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

public interface PlayerAPIExecutor {

  void executeSendMessage(UUID player, String message);

  void executeKickPlayer(UUID player, String message);

  void executePlaySound(UUID player, String sound, float f1, float f2);

  void executeSendTitle(UUID player, String title, String subTitle, int fadeIn,
                        int stay, int fadeOut);

  void executePlayEffect(UUID player, String entityEffect);

  <T> void executePlayEffect(UUID player, String effect, @Nullable T data);

  void executeRespawn(UUID player);

  void executeTeleport(UUID player, String world, double x, double y, double z,
                       float yaw, float pitch);

  void executeConnect(UUID player, String server);

  void executeConnect(UUID player, ProcessInformation server);

  void executeConnect(UUID player, UUID target);

  void executeSetResourcePack(UUID player, String pack);
}
