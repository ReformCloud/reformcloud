/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
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
package systems.reformcloud.reformcloud2.executor.api.provider;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;

/**
 * Provides the possibility to communicate with other processes in the network on an event based system
 * instead of using an own packet implementation
 */
public interface ChannelMessageProvider {

  /**
   * Sends a channel message to a specific process specified by the information of it
   *
   * @param receiver The process information of the receiver the channel message should get sent to
   * @param channel  The name of the channel which can easily identify the message instead of always
   *                 checking the content of it
   * @param data     The data as json which should get sent to the process
   */
  void sendChannelMessage(@NotNull ProcessInformation receiver, @NotNull String channel, @NotNull JsonConfiguration data);

  /**
   * Sends a channel message to all processes of the specified process group
   *
   * @param processGroup The name of a process group. All of the processes which are started based on
   *                     that group will receive the given channel message
   * @param channel      The name of the channel which can easily identify the message instead of always
   *                     checking the content of it
   * @param data         The data as json which should get sent to all processes of the specified group
   */
  void sendChannelMessage(@NotNull String processGroup, @NotNull String channel, @NotNull JsonConfiguration data);

  /**
   * Sends a channel message to all processes which are connected to the given node
   *
   * @param node    A name of a node. All connected channels to this node will receive the channel message
   * @param channel The name of the channel which can easily identify the message instead of always
   *                checking the content of it
   * @param data    The data as json which should get sent to all processes connected to the specified node
   */
  void publishChannelMessageToAll(@NotNull String node, @NotNull String channel, @NotNull JsonConfiguration data);

  /**
   * Publish a channel message to all processes on all nodes
   *
   * @param channel The name of the channel which can easily identify the message instead of always
   *                checking the content of it
   * @param data    The data as json which should get sent to all processes
   */
  void publishChannelMessage(@NotNull String channel, @NotNull JsonConfiguration data);
}
