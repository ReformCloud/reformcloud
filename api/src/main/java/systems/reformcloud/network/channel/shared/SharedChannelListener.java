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
package systems.reformcloud.network.channel.shared;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.network.channel.NetworkChannel;
import systems.reformcloud.network.channel.listener.ChannelListener;
import systems.reformcloud.network.packet.Packet;
import systems.reformcloud.network.packet.query.QueryManager;
import systems.reformcloud.task.Task;

import java.io.IOException;
import java.util.Optional;

public abstract class SharedChannelListener implements ChannelListener {

  protected final NetworkChannel networkChannel;

  public SharedChannelListener(NetworkChannel networkChannel) {
    this.networkChannel = networkChannel;
  }

  @Override
  public void handle(@NotNull Packet input) {
    if (input.getQueryUniqueID() != null) {
      Optional<Task<Packet>> waitingQuery = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(QueryManager.class).getWaitingQuery(input.getQueryUniqueID());
      if (waitingQuery.isPresent()) {
        waitingQuery.get().complete(input);
        return;
      }
    }

    try {
      input.handlePacketReceive(this, this.networkChannel);
    } catch (final Throwable throwable) {
      System.err.println("Error while handling packet " + input.getId() + "@" + input.getClass().getName());
      throwable.printStackTrace();
    }
  }

  @Override
  public void exceptionCaught(@NotNull NetworkChannel channel, @NotNull Throwable cause) {
    boolean debug = Boolean.getBoolean("systems.reformcloud.debug-net");
    if (!(cause instanceof IOException) && debug) {
      System.err.println("Exception in channel " + channel.getRemoteAddress());
      cause.printStackTrace();
    }
  }

  @Override
  public void readOperationCompleted(@NotNull NetworkChannel channel) {
    channel.flush();
  }
}
