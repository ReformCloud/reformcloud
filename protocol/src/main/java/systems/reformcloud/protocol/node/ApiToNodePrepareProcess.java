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
import systems.reformcloud.configuration.JsonConfiguration;
import systems.reformcloud.enums.EnumUtil;
import systems.reformcloud.group.process.ProcessGroup;
import systems.reformcloud.group.template.Template;
import systems.reformcloud.group.template.builder.DefaultTemplate;
import systems.reformcloud.network.PacketIds;
import systems.reformcloud.network.channel.NetworkChannel;
import systems.reformcloud.network.channel.listener.ChannelListener;
import systems.reformcloud.network.data.ProtocolBuffer;
import systems.reformcloud.process.ProcessInformation;
import systems.reformcloud.process.ProcessState;
import systems.reformcloud.process.builder.ProcessInclusion;
import systems.reformcloud.protocol.ProtocolPacket;
import systems.reformcloud.shared.group.DefaultProcessGroup;
import systems.reformcloud.wrappers.ProcessWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ApiToNodePrepareProcess extends ProtocolPacket {

  private String processGroupName;
  private String node;
  private String displayName;
  private String messageOfTheDay;
  private String targetProcessFactory;
  private ProcessGroup processGroup;
  private Template template;
  private Collection<ProcessInclusion> inclusions = new ArrayList<>();
  private JsonConfiguration extra = JsonConfiguration.newJsonConfiguration();
  private ProcessState initialState = ProcessState.READY;
  private UUID processUniqueId = UUID.randomUUID();
  private int memory = -1;
  private int id = -1;
  private int maxPlayers = -1;

  public ApiToNodePrepareProcess() {
  }

  public ApiToNodePrepareProcess(String processGroupName, String node, String displayName, String messageOfTheDay, String targetProcessFactory, ProcessGroup processGroup, Template template,
                                 Collection<ProcessInclusion> inclusions, JsonConfiguration extra, ProcessState initialState, UUID processUniqueId, int memory, int id, int maxPlayers) {
    this.processGroupName = processGroupName;
    this.node = node;
    this.displayName = displayName;
    this.messageOfTheDay = messageOfTheDay;
    this.targetProcessFactory = targetProcessFactory;
    this.processGroup = processGroup;
    this.template = template;
    this.inclusions = inclusions;
    this.extra = extra;
    this.initialState = initialState;
    this.processUniqueId = processUniqueId;
    this.memory = memory;
    this.id = id;
    this.maxPlayers = maxPlayers;
  }

  @Override
  public int getId() {
    return PacketIds.EMBEDDED_BUS + 70;
  }

  @Override
  public void handlePacketReceive(@NotNull ChannelListener reader, @NotNull NetworkChannel channel) {
    ProcessWrapper result = ProcessInformation.builder(this.processGroupName)
      .node(this.node)
      .displayName(this.displayName)
      .messageOfTheDay(this.messageOfTheDay)
      .targetProcessFactory(this.targetProcessFactory)

      .group(this.processGroup)
      .template(this.template)
      .inclusions(this.inclusions)
      .extra(this.extra)
      .initialState(this.initialState)
      .uniqueId(this.processUniqueId)

      .memory(this.memory)
      .id(this.id)
      .maxPlayers(this.maxPlayers)
      .prepare()
      .getUninterruptedly(TimeUnit.SECONDS, 15);
    if (result != null) {
      channel.sendQueryResult(this.getQueryUniqueID(), new ApiToNodeGetProcessInformationResult(result.getProcessInformation()));
    } else {
      channel.sendQueryResult(this.getQueryUniqueID(), new ApiToNodeGetProcessInformationResult());
    }
  }

  @Override
  public void write(@NotNull ProtocolBuffer buffer) {
    buffer.writeString(this.processGroupName);
    buffer.writeString(this.node);
    buffer.writeString(this.displayName);
    buffer.writeString(this.messageOfTheDay);
    buffer.writeString(this.targetProcessFactory);

    buffer.writeObject(this.processGroup);
    buffer.writeObject(this.template);
    buffer.writeObjects(this.inclusions);
    buffer.writeString(this.extra.toString());
    buffer.writeInt(this.initialState.ordinal());
    buffer.writeUniqueId(this.processUniqueId);

    buffer.writeInt(this.memory);
    buffer.writeInt(this.id);
    buffer.writeInt(this.maxPlayers);
  }

  @Override
  public void read(@NotNull ProtocolBuffer buffer) {
    this.processGroupName = buffer.readString();
    this.node = buffer.readString();
    this.displayName = buffer.readString();
    this.messageOfTheDay = buffer.readString();
    this.targetProcessFactory = buffer.readString();

    this.processGroup = buffer.readObject(DefaultProcessGroup.class);
    this.template = buffer.readObject(DefaultTemplate.class);
    this.inclusions = buffer.readObjects(ProcessInclusion.class);
    this.extra = JsonConfiguration.newJsonConfiguration(buffer.readString());
    this.initialState = EnumUtil.findEnumFieldByIndex(ProcessState.class, buffer.readInt()).orElse(null);
    this.processUniqueId = buffer.readUniqueId();

    this.memory = buffer.readInt();
    this.id = buffer.readInt();
    this.maxPlayers = buffer.readInt();
  }
}
