package systems.reformcloud.reformcloud2.executor.api.common.api.player;

import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

public interface PlayerSyncAPI {

  /**
   * Sends a message to a player
   *
   * @param player The uuid of the player which should receive the message
   * @param message The message which should be sent
   */
  void sendMessage(@Nonnull UUID player, @Nonnull String message);

  /**
   * Kicks a player from the network
   *
   * @param player The uuid of the player which should be kicked
   * @param message The kick message
   */
  void kickPlayer(@Nonnull UUID player, @Nonnull String message);

  /**
   * Kicks a player from a specific server
   *
   * @param player The player which should be kicked
   * @param message The kick message
   */
  void kickPlayerFromServer(@Nonnull UUID player, @Nonnull String message);

  /**
   * Plays a sound to a player
   *
   * @param player The uuid of the player which should hear the sound
   * @param sound The sound which should be played
   * @param f1 The volume of the sound
   * @param f2 The pitch of the sound
   */
  void playSound(@Nonnull UUID player, @Nonnull String sound, float f1,
                 float f2);

  /**
   * Sends a title to a player
   *
   * @param player The uuid of the player which should receive the title
   * @param title The title which should be shown
   * @param subTitle The subtitle which should be shown
   * @param fadeIn The fadein time of the title
   * @param stay The stay time, how long the title should stay
   * @param fadeOut The fadeout time of the title
   */
  void sendTitle(@Nonnull UUID player, @Nonnull String title,
                 @Nonnull String subTitle, int fadeIn, int stay, int fadeOut);

  /**
   * Sets a player effect
   *
   * @param player The uuid of the player who should get the effect
   * @param entityEffect The entity effect which should be played
   */
  void playEffect(@Nonnull UUID player, @Nonnull String entityEffect);

  /**
   * Plays a player effect
   *
   * @param player The uuid of the player which should be sent
   * @param effect The effect which should be played
   * @param data A bit needed for some effects
   * @param <T> The data depending to the effect
   */
  <T> void playEffect(@Nonnull UUID player, @Nonnull String effect,
                      @Nullable T data);

  /**
   * Respawn a player
   *
   * @param player The uuid of the player which should be re-spawned
   */
  void respawn(@Nonnull UUID player);

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
   */
  void teleport(@Nonnull UUID player, @Nonnull String world, double x, double y,
                double z, float yaw, float pitch);

  /**
   * Connects a player to a specific server
   *
   * @param player The player who should be connected
   * @param server The target server
   */
  void connect(@Nonnull UUID player, @Nonnull String server);

  /**
   * Connects a player to a specific server
   *
   * @param player The player who should be connected
   * @param server The {@link ProcessInformation} of the target server
   */
  void connect(@Nonnull UUID player, @Nonnull ProcessInformation server);

  /**
   * Connects a player to an other player
   *
   * @param player The player who should be connected
   * @param target The target player
   */
  void connect(@Nonnull UUID player, @Nonnull UUID target);

  /**
   * Sets a player resource pack
   *
   * @param player The player who should get the resource pack
   * @param pack The url of the resource pack
   */
  void setResourcePack(@Nonnull UUID player, @Nonnull String pack);
}
