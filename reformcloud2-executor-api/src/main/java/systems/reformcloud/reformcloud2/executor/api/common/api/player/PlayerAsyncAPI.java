package systems.reformcloud.reformcloud2.executor.api.common.api.player;

import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public interface PlayerAsyncAPI extends PlayerSyncAPI {

    /**
     * Sends a message to a player
     *
     * @param player The uuid of the player which should receive the message
     * @param message The message which should be sent
     * @return A task which will be completed after the completion of the packet sent
     */
    @Nonnull
    @CheckReturnValue
    Task<Void> sendMessageAsync(@Nonnull UUID player, @Nonnull String message);

    /**
     * Kicks a player from the network
     *
     * @param player The uuid of the player which should be kicked
     * @param message The kick message
     * @return A task which will be completed after the completion of the packet sent
     */
    @Nonnull
    @CheckReturnValue
    Task<Void> kickPlayerAsync(@Nonnull UUID player, @Nonnull String message);

    /**
     * Kicks a player from a specific server
     *
     * @param player The player which should be kicked
     * @param message The kick message
     * @return A task which will be completed after the completion of the packet sent
     */
    @Nonnull
    @CheckReturnValue
    Task<Void> kickPlayerFromServerAsync(@Nonnull UUID player, @Nonnull String message);

    /**
     * Plays a sound to a player
     *
     * @param player The uuid of the player which should hear the sound
     * @param sound The sound which should be played
     * @param f1 The volume of the sound
     * @param f2 The pitch of the sound
     * @return A task which will be completed after the completion of the packet sent
     */
    @Nonnull
    @CheckReturnValue
    Task<Void> playSoundAsync(@Nonnull UUID player, @Nonnull String sound, float f1, float f2);

    /**
     * Sends a title to a player
     *
     * @param player The uuid of the player which should receive the title
     * @param title The title which should be shown
     * @param subTitle The subtitle which should be shown
     * @param fadeIn The fadein time of the title
     * @param stay The stay time, how long the title should stay
     * @param fadeOut The fadeout time of the title
     * @return A task which will be completed after the completion of the packet sent
     */
    @Nonnull
    @CheckReturnValue
    Task<Void> sendTitleAsync(@Nonnull UUID player, @Nonnull String title, @Nonnull String subTitle, int fadeIn, int stay, int fadeOut);

    /**
     * Sets a player effect
     *
     * @param player The uuid of the player who should get the effect
     * @param entityEffect The entity effect which should be played
     * @return A task which will be completed after the completion of the packet sent
     */
    @Nonnull
    @CheckReturnValue
    Task<Void> playEffectAsync(@Nonnull UUID player, @Nonnull String entityEffect);

    /**
     * Plays a player effect
     *
     * @param player The uuid of the player which should be sent
     * @param effect The effect which should be played
     * @param data A bit needed for some effects
     * @param <T> The data depending to the effect
     * @return A task which will be completed after the completion of the packet sent
     */
    @Nonnull
    @CheckReturnValue
    <T> Task<Void> playEffectAsync(@Nonnull UUID player, @Nonnull String effect, @Nullable T data);

    /**
     * Respawn a player
     *
     * @param player The uuid of the player which should be re-spawned
     * @return A task which will be completed after the completion of the packet sent
     */
    @Nonnull
    @CheckReturnValue
    Task<Void> respawnAsync(@Nonnull UUID player);

    /**
     * Teleports a player
     *
     * @param player The uuid of the player which should be teleported
     * @param world The name of the world where the player should be teleported to
     * @param x The x coordinate of the new location
     * @param y The y coordinate of the new location
     * @param z The z coordinate of the new location
     * @param yaw The yaw of the new location
     * @param pitch The pitch of the new location
     * @return A task which will be completed after the completion of the packet sent
     */
    @Nonnull
    @CheckReturnValue
    Task<Void> teleportAsync(@Nonnull UUID player, @Nonnull String world, double x, double y, double z, float yaw, float pitch);

    /**
     * Connects a player to a specific server
     *
     * @param player The player who should be connected
     * @param server The target server
     * @return A task which will be completed after the completion of the packet sent
     */
    @Nonnull
    @CheckReturnValue
    Task<Void> connectAsync(@Nonnull UUID player, @Nonnull String server);

    /**
     * Connects a player to a specific server
     *
     * @param player The player who should be connected
     * @param server The {@link ProcessInformation} of the target server
     * @return A task which will be completed after the completion of the packet sent
     */
    @Nonnull
    @CheckReturnValue
    Task<Void> connectAsync(@Nonnull UUID player, @Nonnull ProcessInformation server);

    /**
     * Connects a player to an other player
     *
     * @param player The player who should be connected
     * @param target The target player
     * @return A task which will be completed after the completion of the packet sent
     */
    @Nonnull
    @CheckReturnValue
    Task<Void> connectAsync(@Nonnull UUID player, @Nonnull UUID target);

    /**
     * Sets a player resource pack
     *
     * @param player The player who should get the resource pack
     * @param pack The url of the resource pack
     * @return A task which will be completed after the completion of the packet sent
     */
    @Nonnull
    @CheckReturnValue
    Task<Void> setResourcePackAsync(@Nonnull UUID player, @Nonnull String pack);
}
