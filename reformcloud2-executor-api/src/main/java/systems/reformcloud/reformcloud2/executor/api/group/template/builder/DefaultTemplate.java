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
package systems.reformcloud.reformcloud2.executor.api.group.template.builder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.configuration.data.DefaultJsonDataHolder;
import systems.reformcloud.reformcloud2.executor.api.group.template.Template;
import systems.reformcloud.reformcloud2.executor.api.group.template.inclusion.Inclusion;
import systems.reformcloud.reformcloud2.executor.api.group.template.runtime.DefaultRuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.template.runtime.RuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.template.version.DefaultVersion;
import systems.reformcloud.reformcloud2.executor.api.group.template.version.Version;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class DefaultTemplate extends DefaultJsonDataHolder<Template> implements Template {

  private int priority;
  private boolean global;
  private boolean autoCopyOnClose;
  private String backend;
  private String serverNameSplitter;
  private RuntimeConfiguration runtimeConfiguration;
  private Version version;
  private Collection<Inclusion> templateInclusions;
  private Collection<Inclusion> pathInclusions;
  private String name;

  protected DefaultTemplate() {
    super();
  }

  protected DefaultTemplate(int priority, boolean global, boolean autoCopyOnClose, String backend, String serverNameSplitter,
                            RuntimeConfiguration runtimeConfiguration, Version version, Collection<Inclusion> templateInclusions,
                            Collection<Inclusion> pathInclusions, String name, JsonConfiguration data) {
    super(data);
    this.priority = priority;
    this.global = global;
    this.autoCopyOnClose = autoCopyOnClose;
    this.backend = backend;
    this.serverNameSplitter = serverNameSplitter;
    this.runtimeConfiguration = runtimeConfiguration;
    this.version = version;
    this.templateInclusions = templateInclusions;
    this.pathInclusions = pathInclusions;
    this.name = name;
  }

  @Override
  public int getPriority() {
    return this.priority;
  }

  @Override
  public void setPriority(int priority) {
    this.priority = priority;
  }

  @Override
  public boolean isGlobal() {
    return this.global;
  }

  @Override
  public void setGlobal(boolean global) {
    this.global = global;
  }

  @Override
  public boolean isAutoCopyOnClose() {
    return this.autoCopyOnClose;
  }

  @Override
  public void setAutoCopyOnClose(boolean autoCopyOnClose) {
    this.autoCopyOnClose = autoCopyOnClose;
  }

  @Override
  public @NotNull String getBackend() {
    return this.backend;
  }

  @Override
  public void setBackend(@NotNull String backend) {
    this.backend = backend;
  }

  @Override
  public @Nullable String getServerNameSplitter() {
    return this.serverNameSplitter;
  }

  @Override
  public void setServerNameSplitter(@Nullable String serverNameSplitter) {
    this.serverNameSplitter = serverNameSplitter;
  }

  @Override
  public @NotNull RuntimeConfiguration getRuntimeConfiguration() {
    return this.runtimeConfiguration;
  }

  @Override
  public void setRuntimeConfiguration(@NotNull RuntimeConfiguration runtimeConfiguration) {
    this.runtimeConfiguration = runtimeConfiguration;
  }

  @Override
  public @NotNull Version getVersion() {
    return this.version;
  }

  @Override
  public void setVersion(@NotNull Version version) {
    this.version = version;
  }

  @Override
  public @NotNull Template clone() {
    return new DefaultTemplate(
      this.priority,
      this.global,
      this.autoCopyOnClose,
      this.backend,
      this.serverNameSplitter,
      this.runtimeConfiguration.clone(),
      this.version,
      new ArrayList<>(this.templateInclusions),
      new ArrayList<>(this.pathInclusions),
      this.name,
      JsonConfiguration.newJsonConfiguration(this.dataHolder.getBackingObject())
    );
  }

  @Override
  public int compareTo(@NotNull Template template) {
    return Integer.compare(this.getPriority(), template.getPriority());
  }

  @Override
  public @NotNull @UnmodifiableView Collection<Inclusion> getTemplateInclusions() {
    return Collections.unmodifiableCollection(this.templateInclusions);
  }

  @Override
  public @NotNull @UnmodifiableView Collection<Inclusion> getTemplateInclusions(Inclusion.@NotNull InclusionLoadType loadType) {
    return this.templateInclusions.stream()
      .filter(inclusion -> inclusion.getInclusionLoadType() == loadType)
      .collect(Collectors.toList());
  }

  @Override
  public @NotNull Optional<Inclusion> getTemplateInclusion(@NotNull String template) {
    return this.templateInclusions.stream()
      .filter(inclusion -> inclusion.getKey().equals(template))
      .findFirst();
  }

  @Override
  public void addTemplateInclusions(@NotNull Inclusion inclusion) {
    this.templateInclusions.add(inclusion);
  }

  @Override
  public void removeTemplateInclusion(@NotNull Inclusion inclusion) {
    this.templateInclusions.remove(inclusion);
  }

  @Override
  public void removeTemplateInclusion(@NotNull String template) {
    this.getTemplateInclusion(template).ifPresent(this.templateInclusions::remove);
  }

  @Override
  public void removeAllTemplateInclusions() {
    this.templateInclusions.clear();
  }

  @Override
  public @NotNull @UnmodifiableView Collection<Inclusion> getPathInclusions() {
    return Collections.unmodifiableCollection(this.pathInclusions);
  }

  @Override
  public @NotNull @UnmodifiableView Collection<Inclusion> getPathInclusions(Inclusion.@NotNull InclusionLoadType loadType) {
    return this.pathInclusions.stream()
      .filter(inclusion -> inclusion.getInclusionLoadType() == loadType)
      .collect(Collectors.toList());
  }

  @Override
  public @NotNull Optional<Inclusion> getPathInclusion(@NotNull String path) {
    return this.pathInclusions.stream()
      .filter(inclusion -> inclusion.getKey().equals(path))
      .findFirst();
  }

  @Override
  public void addPathInclusions(@NotNull Inclusion inclusion) {
    this.pathInclusions.add(inclusion);
  }

  @Override
  public void removePathInclusion(@NotNull Inclusion inclusion) {
    this.pathInclusions.remove(inclusion);
  }

  @Override
  public void removePathInclusion(@NotNull String path) {
    this.getPathInclusion(path).ifPresent(this.pathInclusions::remove);
  }

  @Override
  public void removeAllPathInclusions() {
    this.pathInclusions.clear();
  }

  @Override
  public @NotNull Template self() {
    return this;
  }

  @Override
  public void write(@NotNull ProtocolBuffer buffer) {
    buffer.writeInt(this.priority);
    buffer.writeBoolean(this.global);
    buffer.writeBoolean(this.autoCopyOnClose);
    buffer.writeString(this.backend);
    buffer.writeString(this.serverNameSplitter);
    buffer.writeObject(this.runtimeConfiguration);
    buffer.writeObject(this.version);
    buffer.writeObjects(this.templateInclusions);
    buffer.writeObjects(this.pathInclusions);
    buffer.writeString(this.name);
    super.write(buffer);
  }

  @Override
  public void read(@NotNull ProtocolBuffer buffer) {
    this.priority = buffer.readInt();
    this.global = buffer.readBoolean();
    this.autoCopyOnClose = buffer.readBoolean();
    this.backend = buffer.readString();
    this.serverNameSplitter = buffer.readString();
    this.runtimeConfiguration = buffer.readObject(DefaultRuntimeConfiguration.class);
    this.version = buffer.readObject(DefaultVersion.class, Version.class);
    this.templateInclusions = buffer.readObjects(Inclusion.class);
    this.pathInclusions = buffer.readObjects(Inclusion.class);
    this.name = buffer.readString();
    super.read(buffer);
  }

  @Override
  public @NotNull String getName() {
    return this.name;
  }

  @Override
  public void setName(@NotNull String newName) {
    this.name = newName;
  }
}
