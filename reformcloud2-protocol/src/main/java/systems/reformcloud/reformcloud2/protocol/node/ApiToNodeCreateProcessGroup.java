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
package systems.reformcloud.reformcloud2.protocol.node;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.group.process.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.group.process.player.DefaultPlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.process.player.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.process.startup.DefaultStartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.process.startup.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.template.Template;
import systems.reformcloud.reformcloud2.executor.api.group.template.builder.DefaultTemplate;
import systems.reformcloud.reformcloud2.executor.api.network.PacketIds;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.channel.listener.ChannelListener;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.protocol.ProtocolPacket;

import java.util.List;

public class ApiToNodeCreateProcessGroup extends ProtocolPacket {

  private String name;
  private boolean staticGroup;
  private boolean lobby;
  private boolean showId;
  private List<Template> templates;
  private PlayerAccessConfiguration playerAccessConfiguration;
  private StartupConfiguration startupConfiguration;

  public ApiToNodeCreateProcessGroup() {
  }

  public ApiToNodeCreateProcessGroup(String name, boolean staticGroup, boolean lobby, boolean showId,
                                     List<Template> templates, PlayerAccessConfiguration playerAccessConfiguration,
                                     StartupConfiguration startupConfiguration) {
    this.name = name;
    this.staticGroup = staticGroup;
    this.lobby = lobby;
    this.showId = showId;
    this.templates = templates;
    this.playerAccessConfiguration = playerAccessConfiguration;
    this.startupConfiguration = startupConfiguration;
  }

  @Override
  public int getId() {
    return PacketIds.EMBEDDED_BUS + 59;
  }

  @Override
  public void handlePacketReceive(@NotNull ChannelListener reader, @NotNull NetworkChannel channel) {
    ProcessGroup processGroup = ProcessGroup.builder(this.name)
      .staticGroup(this.staticGroup)
      .lobby(this.lobby)
      .showId(this.showId)
      .templates(this.templates)
      .playerAccessConfig(this.playerAccessConfiguration)
      .startupConfiguration(this.startupConfiguration)
      .createPermanently()
      .getUninterruptedly();
    channel.sendQueryResult(this.getQueryUniqueID(), new ApiToNodeCreateProcessGroupResult(processGroup));
  }

  @Override
  public void write(@NotNull ProtocolBuffer buffer) {
    buffer.writeString(this.name);
    buffer.writeBoolean(this.staticGroup);
    buffer.writeBoolean(this.lobby);
    buffer.writeBoolean(this.showId);
    buffer.writeObjects(this.templates);
    buffer.writeObject(this.playerAccessConfiguration);
    buffer.writeObject(this.startupConfiguration);
  }

  @Override
  public void read(@NotNull ProtocolBuffer buffer) {
    this.name = buffer.readString();
    this.staticGroup = buffer.readBoolean();
    this.lobby = buffer.readBoolean();
    this.showId = buffer.readBoolean();
    this.templates = buffer.readObjects(DefaultTemplate.class, Template.class);
    this.playerAccessConfiguration = buffer.readObject(DefaultPlayerAccessConfiguration.class);
    this.startupConfiguration = buffer.readObject(DefaultStartupConfiguration.class);
  }
}
