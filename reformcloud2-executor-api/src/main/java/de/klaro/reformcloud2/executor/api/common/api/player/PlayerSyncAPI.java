package de.klaro.reformcloud2.executor.api.common.api.player;

import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.common.utility.annotiations.Nullable;

import java.util.UUID;

public interface PlayerSyncAPI {

    void sendMessage(UUID player, String message);

    void kickPlayer(UUID player, String message);

    void kickPlayerFromServer(UUID player, String message);

    void playSound(UUID player, String sound, float f1, float f2);

    void sendTitle(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut);

    void playEffect(UUID player, String entityEffect);

    <T> void playEffect(UUID player, String effect, @Nullable T data);

    void respawn(UUID player);

    void teleport(UUID player, String world, double x, double y, double z, float yaw, float pitch);

    void connect(UUID player, String server);

    void connect(UUID player, ProcessInformation server);

    void connect(UUID player, UUID target);

    void setResourcePack(UUID player, String pack);
}
