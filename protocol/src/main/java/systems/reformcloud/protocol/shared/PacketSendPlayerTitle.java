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
package systems.reformcloud.protocol.shared;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.network.PacketIds;
import systems.reformcloud.network.channel.NetworkChannel;
import systems.reformcloud.network.channel.listener.ChannelListener;
import systems.reformcloud.network.data.ProtocolBuffer;
import systems.reformcloud.protocol.ProtocolPacket;

import java.util.UUID;

public class PacketSendPlayerTitle extends ProtocolPacket {

  private UUID uniqueId;
  private String title;
  private String subTitle;
  private long fadeIn;
  private long stay;
  private long fadeOut;

  public PacketSendPlayerTitle() {
  }

  public PacketSendPlayerTitle(UUID uniqueId, String title, String subTitle, long fadeIn, long stay, long fadeOut) {
    this.uniqueId = uniqueId;
    this.title = title;
    this.subTitle = subTitle;
    this.fadeIn = fadeIn;
    this.stay = stay;
    this.fadeOut = fadeOut;
  }

  public UUID getUniqueId() {
    return this.uniqueId;
  }

  public String getTitle() {
    return this.title;
  }

  public String getSubTitle() {
    return this.subTitle;
  }

  public long getFadeIn() {
    return this.fadeIn;
  }

  public long getStay() {
    return this.stay;
  }

  public long getFadeOut() {
    return this.fadeOut;
  }

  @Override
  public int getId() {
    return PacketIds.API_BUS + 12;
  }

  @Override
  public void handlePacketReceive(@NotNull ChannelListener reader, @NotNull NetworkChannel channel) {
    super.post(channel, PacketSendPlayerTitle.class, this);
  }

  @Override
  public void write(@NotNull ProtocolBuffer buffer) {
    buffer.writeUniqueId(this.uniqueId);
    buffer.writeString(this.title);
    buffer.writeString(this.subTitle);
    buffer.writeLong(this.fadeIn);
    buffer.writeLong(this.stay);
    buffer.writeLong(this.fadeOut);
  }

  @Override
  public void read(@NotNull ProtocolBuffer buffer) {
    this.uniqueId = buffer.readUniqueId();
    this.title = buffer.readString();
    this.subTitle = buffer.readString();
    this.fadeIn = buffer.readLong();
    this.stay = buffer.readLong();
    this.fadeOut = buffer.readLong();
  }
}
