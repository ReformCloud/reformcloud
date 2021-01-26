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
package systems.reformcloud.reformcloud2.shared.group;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.configuration.data.DefaultJsonDataHolder;
import systems.reformcloud.reformcloud2.executor.api.group.process.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.group.process.player.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.process.startup.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.template.Template;
import systems.reformcloud.reformcloud2.executor.api.group.template.builder.DefaultTemplate;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.process.builder.ProcessBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DefaultProcessGroup extends DefaultJsonDataHolder<ProcessGroup> implements ProcessGroup {

  private StartupConfiguration startupConfiguration;
  private PlayerAccessConfiguration playerAccessConfiguration;
  private boolean showIdInName;
  private boolean createsStaticProcesses;
  private boolean lobbyGroup;
  private Collection<Template> templates;
  private String name;

  public DefaultProcessGroup() {
  }

  public DefaultProcessGroup(StartupConfiguration startupConfiguration, PlayerAccessConfiguration playerAccessConfiguration,
                             boolean showIdInName, boolean createsStaticProcesses, boolean lobbyGroup,
                             Collection<Template> templates, String name) {
    this.startupConfiguration = startupConfiguration;
    this.playerAccessConfiguration = playerAccessConfiguration;
    this.showIdInName = showIdInName;
    this.createsStaticProcesses = createsStaticProcesses;
    this.lobbyGroup = lobbyGroup;
    this.templates = templates;
    this.name = name;
  }

  @Override
  public @NotNull StartupConfiguration getStartupConfiguration() {
    return this.startupConfiguration;
  }

  @Override
  public void setStartupConfiguration(@NotNull StartupConfiguration startupConfiguration) {
    this.startupConfiguration = startupConfiguration;
  }

  @Override
  public @NotNull PlayerAccessConfiguration getPlayerAccessConfiguration() {
    return this.playerAccessConfiguration;
  }

  @Override
  public void setPlayerAccessConfiguration(@NotNull PlayerAccessConfiguration playerAccessConfiguration) {
    this.playerAccessConfiguration = playerAccessConfiguration;
  }

  @Override
  public boolean showIdInName() {
    return this.showIdInName;
  }

  @Override
  public void setShowIdInName(boolean showIdInName) {
    this.showIdInName = showIdInName;
  }

  @Override
  public boolean createsStaticProcesses() {
    return this.createsStaticProcesses;
  }

  @Override
  public void setCreatesStaticProcesses(boolean createsStaticProcesses) {
    this.createsStaticProcesses = createsStaticProcesses;
  }

  @Override
  public boolean isLobbyGroup() {
    return this.lobbyGroup;
  }

  @Override
  public void setLobbyGroup(boolean lobbyGroup) {
    this.lobbyGroup = lobbyGroup;
  }

  @Override
  public @NotNull ProcessBuilder newProcess() {
    return ExecutorAPI.getInstance().getProcessProvider().createProcess().group(this);
  }

  @Override
  public void update() {
    ExecutorAPI.getInstance().getProcessGroupProvider().updateProcessGroup(this);
  }

  @Override
  public @NotNull ProcessGroup clone() {
    return new DefaultProcessGroup(
      this.startupConfiguration.clone(),
      this.playerAccessConfiguration.clone(),
      this.showIdInName,
      this.createsStaticProcesses,
      this.lobbyGroup,
      new ArrayList<>(this.templates),
      this.name
    );
  }

  @Override
  public @NotNull @UnmodifiableView Collection<Template> getTemplates() {
    return Collections.unmodifiableCollection(this.templates);
  }

  @Override
  public void setTemplates(@NotNull List<Template> templates) {
    this.templates = new ArrayList<>(templates);
  }

  @Override
  public @NotNull Optional<Template> getTemplate(@NotNull String name) {
    return this.templates.stream().filter(template -> template.getName().equals(name)).findFirst();
  }

  @Override
  public void addTemplate(@NotNull Template template) {
    this.templates.add(template);
  }

  @Override
  public void removeTemplate(@NotNull Template template) {
    this.templates.remove(template);
  }

  @Override
  public void removeTemplate(@NotNull String name) {
    this.getTemplate(name).ifPresent(this.templates::remove);
  }

  @Override
  public boolean isTemplatePresent(@NotNull String name) {
    return this.getTemplate(name).isPresent();
  }

  @Override
  public void removeAllTemplates() {
    this.templates.clear();
  }

  @Override
  public void write(@NotNull ProtocolBuffer buffer) {
    buffer.writeObject(this.startupConfiguration);
    buffer.writeObject(this.playerAccessConfiguration);
    buffer.writeBoolean(this.showIdInName);
    buffer.writeBoolean(this.createsStaticProcesses);
    buffer.writeBoolean(this.lobbyGroup);
    buffer.writeObjects(this.templates);
    buffer.writeString(this.name);
    super.write(buffer);
  }

  @Override
  public void read(@NotNull ProtocolBuffer buffer) {
    this.startupConfiguration = buffer.readObject(StartupConfiguration.class);
    this.playerAccessConfiguration = buffer.readObject(PlayerAccessConfiguration.class);
    this.showIdInName = buffer.readBoolean();
    this.createsStaticProcesses = buffer.readBoolean();
    this.lobbyGroup = buffer.readBoolean();
    this.templates = buffer.readObjects(DefaultTemplate.class, Template.class);
    this.name = buffer.readString();
    super.read(buffer);
  }

  @Override
  public @NotNull ProcessGroup self() {
    return this;
  }

  @Override
  public @NotNull String getName() {
    return this.name;
  }
}
