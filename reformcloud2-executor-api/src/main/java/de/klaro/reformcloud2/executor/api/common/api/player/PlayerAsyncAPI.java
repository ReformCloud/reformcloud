package de.klaro.reformcloud2.executor.api.common.api.player;

import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.common.utility.annotiations.Nullable;
import de.klaro.reformcloud2.executor.api.common.utility.task.Task;

import java.util.UUID;

public interface PlayerAsyncAPI extends PlayerSyncAPI {

    Task<Void> sendMessageAsync(UUID player, String message);

    Task<Void> kickPlayerAsync(UUID player, String message);

    Task<Void> kickPlayerFromServerAsync(UUID player, String message);

    Task<Void> playSoundAsync(UUID player, String sound, float f1, float f2);

    Task<Void> sendTitleAsync(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut);

    Task<Void> playEffectAsync(UUID player, String entityEffect);

    <T> Task<Void> playEffectAsync(UUID player, String effect, @Nullable T data);

    Task<Void> respawnAsync(UUID player);

    Task<Void> teleportAsync(UUID player, String world, double x, double y, double z, float yaw, float pitch);

    Task<Void> connectAsync(UUID player, String server);

    Task<Void> connectAsync(UUID player, ProcessInformation server);

    Task<Void> connectAsync(UUID player, UUID target);

    Task<Void> setResourcePackAsync(UUID player, String pack);
}
