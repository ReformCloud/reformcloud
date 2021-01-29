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
package systems.reformcloud.protocol.node;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.network.PacketIds;
import systems.reformcloud.network.channel.NetworkChannel;
import systems.reformcloud.network.channel.listener.ChannelListener;
import systems.reformcloud.network.data.ProtocolBuffer;
import systems.reformcloud.protocol.ProtocolPacket;
import systems.reformcloud.wrappers.NodeProcessWrapper;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class ApiToNodeCompleteCommandLine extends ProtocolPacket {

  private UUID nodeUniqueId;
  private String commandLine;

  public ApiToNodeCompleteCommandLine() {
  }

  public ApiToNodeCompleteCommandLine(UUID nodeUniqueId, String commandLine) {
    this.nodeUniqueId = nodeUniqueId;
    this.commandLine = commandLine;
  }

  @Override
  public int getId() {
    return PacketIds.EMBEDDED_BUS + 39;
  }

  @Override
  public void handlePacketReceive(@NotNull ChannelListener reader, @NotNull NetworkChannel channel) {
    Optional<NodeProcessWrapper> wrapper = ExecutorAPI.getInstance().getNodeInformationProvider().getNodeInformation(this.nodeUniqueId);
    if (wrapper.isPresent()) {
      channel.sendQueryResult(this.getQueryUniqueID(), new ApiToNodeGetStringCollectionResult(wrapper.get().tabCompleteCommandLine(this.commandLine)));
    } else {
      channel.sendQueryResult(this.getQueryUniqueID(), new ApiToNodeGetStringCollectionResult(new ArrayList<>()));
    }
  }

  @Override
  public void write(@NotNull ProtocolBuffer buffer) {
    buffer.writeUniqueId(this.nodeUniqueId);
    buffer.writeString(this.commandLine);
  }

  @Override
  public void read(@NotNull ProtocolBuffer buffer) {
    this.nodeUniqueId = buffer.readUniqueId();
    this.commandLine = buffer.readString();
  }
}
