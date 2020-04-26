package systems.reformcloud.reformcloud2.executor.api.common.api.player;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.util.UUID;

public interface PlayerAsyncAPI {

    /**
     * Sends a message to a player
     *
     * @param player  The uuid of the player which should receive the message
     * @param message The message which should be sent
     * @return A task which will be completed after the completion of the packet sent
     */
    @NotNull
    Task<Void> sendMessageAsync(@NotNull UUID player, @NotNull String message);

    /**
     * Kicks a player from the network
     *
     * @param player  The uuid of the player which should be kicked
     * @param message The kick message
     * @return A task which will be completed after the completion of the packet sent
     */
    @NotNull
    Task<Void> kickPlayerAsync(@NotNull UUID player, @NotNull String message);

    /**
     * Kicks a player from a specific server
     *
     * @param player  The player which should be kicked
     * @param message The kick message
     * @return A task which will be completed after the completion of the packet sent
     */
    @NotNull
    Task<Void> kickPlayerFromServerAsync(@NotNull UUID player, @NotNull String message);

    /**
     * Plays a sound to a player
     *
     * @param player The uuid of the player which should hear the sound
     * @param sound  The sound which should be played
     * @param f1     The volume of the sound
     * @param f2     The pitch of the sound
     * @return A task which will be completed after the completion of the packet sent
     */
    @NotNull
    Task<Void> playSoundAsync(@NotNull UUID player, @NotNull String sound, float f1, float f2);

    /**
     * Sends a title to a player
     *
     * @param player   The uuid of the player which should receive the title
     * @param title    The title which should be shown
     * @param subTitle The subtitle which should be shown
     * @param fadeIn   The fadein time of the title
     * @param stay     The stay time, how long the title should stay
     * @param fadeOut  The fadeout time of the title
     * @return A task which will be completed after the completion of the packet sent
     */
    @NotNull
    Task<Void> sendTitleAsync(@NotNull UUID player, @NotNull String title, @NotNull String subTitle, int fadeIn, int stay, int fadeOut);

    /**
     * Sets a player effect
     *
     * @param player       The uuid of the player who should get the effect
     * @param entityEffect The entity effect which should be played
     * @return A task which will be completed after the completion of the packet sent
     */
    @NotNull
    Task<Void> playEffectAsync(@NotNull UUID player, @NotNull String entityEffect);

    /**
     * Teleports a player
     *
     * @param player The uuid of the player which should be teleported
     * @param world  The name of the world where the player should be teleported to
     * @param x      The x coordinate of the new location
     * @param y      The y coordinate of the new location
     * @param z      The z coordinate of the new location
     * @param yaw    The yaw of the new location
     * @param pitch  The pitch of the new location
     * @return A task which will be completed after the completion of the packet sent
     */
    @NotNull
    Task<Void> teleportAsync(@NotNull UUID player, @NotNull String world, double x, double y, double z, float yaw, float pitch);

    /**
     * Connects a player to a specific server
     *
     * @param player The player who should be connected
     * @param server The target server
     * @return A task which will be completed after the completion of the packet sent
     */
    @NotNull
    Task<Void> connectAsync(@NotNull UUID player, @NotNull String server);

    /**
     * Connects a player to a specific server
     *
     * @param player The player who should be connected
     * @param server The {@link ProcessInformation} of the target server
     * @return A task which will be completed after the completion of the packet sent
     */
    @NotNull
    Task<Void> connectAsync(@NotNull UUID player, @NotNull ProcessInformation server);

    /**
     * Connects a player to an other player
     *
     * @param player The player who should be connected
     * @param target The target player
     * @return A task which will be completed after the completion of the packet sent
     */
    @NotNull
    Task<Void> connectAsync(@NotNull UUID player, @NotNull UUID target);
}
