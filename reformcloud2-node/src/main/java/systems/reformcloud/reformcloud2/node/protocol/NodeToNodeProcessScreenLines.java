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
package systems.reformcloud.reformcloud2.node.protocol;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.language.TranslationHolder;
import systems.reformcloud.reformcloud2.executor.api.network.PacketIds;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.channel.listener.ChannelListener;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.protocol.ProtocolPacket;

import java.util.Collection;

public class NodeToNodeProcessScreenLines extends ProtocolPacket {

  private String processName;
  private String nodeName;
  private Collection<String> newLines;

  public NodeToNodeProcessScreenLines() {
  }

  public NodeToNodeProcessScreenLines(String processName, String nodeName, Collection<String> newLines) {
    this.processName = processName;
    this.nodeName = nodeName;
    this.newLines = newLines;
  }

  @Override
  public int getId() {
    return PacketIds.NODE_BUS + 39;
  }

  @Override
  public void handlePacketReceive(@NotNull ChannelListener reader, @NotNull NetworkChannel channel) {
    for (String line : this.newLines) {
      System.out.println(TranslationHolder.translate(
        "screen-line-added",
        this.processName,
        this.nodeName,
        line
      ));
    }
  }

  @Override
  public void write(@NotNull ProtocolBuffer buffer) {
    buffer.writeString(this.processName);
    buffer.writeString(this.nodeName);
    buffer.writeStringArray(this.newLines);
  }

  @Override
  public void read(@NotNull ProtocolBuffer buffer) {
    this.processName = buffer.readString();
    this.nodeName = buffer.readString();
    this.newLines = buffer.readStringArray();
  }
}
