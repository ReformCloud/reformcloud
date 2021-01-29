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
package systems.reformcloud.reformcloud2.protocol.shared;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.network.PacketIds;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.channel.listener.ChannelListener;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.protocol.ProtocolPacket;

import java.util.Set;
import java.util.UUID;

public class PacketSendBossBar extends ProtocolPacket {

  protected UUID uniqueId;
  protected String name;
  protected float progress;
  protected int color;
  protected int overlay;
  protected Set<Integer> flags;
  protected boolean hide;

  public PacketSendBossBar() {
  }

  public PacketSendBossBar(UUID uniqueId, String name, float progress, int color, int overlay, Set<Integer> flags, boolean hide) {
    this.uniqueId = uniqueId;
    this.name = name;
    this.progress = progress;
    this.color = color;
    this.overlay = overlay;
    this.flags = flags;
    this.hide = hide;
  }

  @Override
  public void write(@NotNull ProtocolBuffer buffer) {
    buffer.writeUniqueId(this.uniqueId);
    buffer.writeString(this.name);
    buffer.writeFloat(this.progress);
    buffer.writeInt(this.color);
    buffer.writeInt(this.overlay);
    buffer.writeIntSet(this.flags);
    buffer.writeBoolean(this.hide);
  }

  @Override
  public void read(@NotNull ProtocolBuffer buffer) {
    this.uniqueId = buffer.readUniqueId();
    this.name = buffer.readString();
    this.progress = buffer.readFloat();
    this.color = buffer.readInt();
    this.overlay = buffer.readInt();
    this.flags = buffer.readIntSet();
    this.hide = buffer.readBoolean();
  }

  @Override
  public int getId() {
    return PacketIds.API_BUS + 21;
  }

  @Override
  public void handlePacketReceive(@NotNull ChannelListener reader, @NotNull NetworkChannel channel) {
    super.post(channel, PacketSendBossBar.class, this);
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getName() {
    return this.name;
  }

  public float getProgress() {
    return this.progress;
  }

  public int getColor() {
    return this.color;
  }

  public int getOverlay() {
    return this.overlay;
  }

  public Set<Integer> getFlags() {
    return this.flags;
  }

  public boolean isHide() {
    return this.hide;
  }
}
