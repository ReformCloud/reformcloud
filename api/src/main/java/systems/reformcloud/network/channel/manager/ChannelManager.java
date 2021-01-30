/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.network.channel.manager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.network.channel.NetworkChannel;

import java.util.Collection;
import java.util.Optional;

/**
 * A manager for {@link NetworkChannel}s.
 */
public interface ChannelManager {
  /**
   * Get a specific channel by it's name.
   *
   * @param name The name of the channel to get.
   * @return The channel with the given {@code name}.
   */
  @NotNull
  Optional<NetworkChannel> getChannel(@NotNull String name);

  /**
   * Gets all channel which are connected from the specified {@code remoteAddress}.
   *
   * @param remoteAddress The address to get the channels for.
   * @return The channels connected from the given address.
   */
  @NotNull
  @UnmodifiableView Collection<NetworkChannel> getNetworkChannels(@NotNull String remoteAddress);

  /**
   * Gets the first channel registered in this holder.
   *
   * @return The first channel registered in this holder.
   */
  @NotNull
  Optional<NetworkChannel> getFirstChannel();

  /**
   * Get all channels registered in this holder.
   *
   * @return All channels registered in this holder.
   */
  @NotNull
  @UnmodifiableView Collection<NetworkChannel> getRegisteredChannels();

  /**
   * Registers the specific {@code channel} to this holder.
   *
   * @param channel The channel to register.
   */
  void registerChannel(@NotNull NetworkChannel channel);

  /**
   * Unregisters the given channel from this holder.
   *
   * @param channel The channel to unregister.
   */
  default void unregisterChannel(@NotNull NetworkChannel channel) {
    this.unregisterChannel(channel.getName());
  }

  /**
   * Unregisters the first channel with the given {@code name} from this holder.
   *
   * @param name The name of the channel to unregister.
   */
  void unregisterChannel(@NotNull String name);
}
