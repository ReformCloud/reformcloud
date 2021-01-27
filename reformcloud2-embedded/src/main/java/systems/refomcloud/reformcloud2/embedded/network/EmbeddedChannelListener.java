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
package systems.refomcloud.reformcloud2.embedded.network;

import org.jetbrains.annotations.NotNull;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.network.PacketIds;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.channel.manager.ChannelManager;
import systems.reformcloud.reformcloud2.executor.api.network.channel.shared.SharedChannelListener;
import systems.reformcloud.reformcloud2.executor.api.network.packet.Packet;
import systems.reformcloud.reformcloud2.protocol.shared.PacketAuthBegin;
import systems.reformcloud.reformcloud2.protocol.shared.PacketAuthSuccess;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class EmbeddedChannelListener extends SharedChannelListener {

  private final Lock lock;
  private final Condition condition;
  private boolean wasActive = false;

  public EmbeddedChannelListener(NetworkChannel channel, Lock lock, Condition condition) {
    super(channel);
    PacketRegister.preAuth();

    this.lock = lock;
    this.condition = condition;
  }

  @Override
  public boolean shouldHandle(@NotNull Packet packet) {
    return super.networkChannel.getName() != null || packet.getId() == PacketIds.AUTH_BUS + 1;
  }

  @Override
  public void channelActive(@NotNull NetworkChannel context) {
    synchronized (this) {
      if (!this.wasActive) {
        this.wasActive = true;
      } else {
        return;
      }
    }

    context.sendPacket(new PacketAuthBegin(
      Embedded.getInstance().getConfig().getConnectionKey(),
      2,
      JsonConfiguration.newJsonConfiguration().add("pid", Embedded.getInstance().getCurrentProcessInformation().getId().getUniqueId())
    ));
  }

  @Override
  public void channelInactive(@NotNull NetworkChannel channel) {
    if (channel.isOpen() && channel.isWritable()) {
      return;
    }

    super.networkChannel.close();
    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class).unregisterChannel(super.networkChannel);
    System.exit(0);
  }

  @Override
  public void channelWriteAbilityChanged(@NotNull NetworkChannel channel) {
  }

  @Override
  public void handle(@NotNull Packet input) {
    if (input.getId() == PacketIds.AUTH_BUS + 1) {
      if (!(input instanceof PacketAuthSuccess)) {
        return;
      }

      super.networkChannel.setName("Controller");
      ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class).registerChannel(super.networkChannel);
      PacketRegister.postAuth();

      try {
        this.lock.lock();
        this.condition.signalAll();
      } finally {
        this.lock.unlock();
      }

      return;
    }

    super.handle(input);
  }
}
