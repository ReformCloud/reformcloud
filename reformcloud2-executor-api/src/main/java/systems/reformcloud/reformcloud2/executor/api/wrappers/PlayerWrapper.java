/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.api.wrappers;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.task.Task;

import java.util.Optional;
import java.util.UUID;

/**
 * A wrapper for a connected player
 */
public interface PlayerWrapper {

    /**
     * Get the proxy process the player is currently on
     *
     * @return An optional process information which is present if the player is currently connected to a proxy
     */
    @NotNull Task<Optional<ProcessInformation>> getConnectedProxy();

    /**
     * Get the server process the player is currently on
     *
     * @return An optional process information which is present if the player is currently connected to a server
     */
    @NotNull Task<Optional<ProcessInformation>> getConnectedServer();

    /**
     * Get the proxy unique id the player is currently on
     *
     * @return An optional unique id which is present if the player is currently connected to a proxy
     */
    @NotNull Optional<UUID> getConnectedProxyUniqueId();

    /**
     * Get the server unique id the player is currently on
     *
     * @return An optional unique id which is present if the player is currently connected to a server
     */
    @NotNull Optional<UUID> getConnectedServerUniqueId();

    /**
     * Sends a message to the current player using the proxy the player is currently connected to.
     * <p>If the player is not connected this method has no effect.</p>
     *
     * @param message The message which should get sent to the player
     */
    void sendMessage(@NotNull String message);

    /**
     * Disconnects the player from his connected proxy.
     * <p>If the player is not connected this method has no effect.</p>
     *
     * @param kickReason The reason for the disconnect
     */
    void disconnect(@NotNull String kickReason);

    /**
     * Plays a sound to the player using it's currently connected server.
     * <p>If the player is not connected this method has no effect.</p>
     *
     * @param sound  The sound to play
     * @param volume The volume of the sound
     * @param pitch  The pitch of the sound
     */
    void playSound(@NotNull String sound, float volume, float pitch);

    /**
     * Sends a title to the player using it's currently connected proxy.
     * <p>If the player is not connected this method has no effect.</p>
     *
     * @param title    Title text
     * @param subTitle Subtitle text
     * @param fadeIn   Time in ticks for titles to fade in. Defaults to 10.
     * @param stay     Time in ticks for titles to stay. Defaults to 70.
     * @param fadeOut  Time in ticks for titles to fade out. Defaults to 20.
     */
    void sendTitle(@NotNull String title, @NotNull String subTitle, int fadeIn, int stay, int fadeOut);

    /**
     * Plays an effect to the player using it's currently connected server.
     * <p>If the player is not connected this method has no effect.</p>
     *
     * @param effect The effect which should get played
     */
    void playEffect(@NotNull String effect);

    /**
     * Set the location of the player to the given location. If the player is riding vehicle, it will
     * be dismounted prior to teleportation. This method uses the player's currently connected server.
     * <p>If the player is not connected this method has no effect.</p>
     *
     * @param world The world the new location is in
     * @param x     The x coordinate of the new location
     * @param y     The y coordinate of the new location
     * @param z     The z coordinate of the new location
     * @param yaw   The yaw rotation of the new location
     * @param pitch The pitch rotation of the new location
     */
    void setLocation(@NotNull String world, double x, double y, double z, float yaw, float pitch);

    /**
     * Connects / transfers the player to the specified server, gracefully closing the current one
     * using the player's currently connected proxy.
     * <p>If the player is not connected this method has no effect.</p>
     *
     * @param server The name of the server to connect to
     */
    void connect(@NotNull String server);

    /**
     * Connects / transfers the player to the specified player's server, gracefully closing the current one
     * using the player's currently connected proxy.
     * <p>If either this or the target player is not connected this method has no effect.</p>
     *
     * @param otherPlayer The player to connect to
     */
    void connect(@NotNull UUID otherPlayer);
}
