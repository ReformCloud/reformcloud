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
package systems.reformcloud.reformcloud2.node.group;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.builder.ProcessGroupBuilder;
import systems.reformcloud.reformcloud2.executor.api.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.process.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.language.TranslationHolder;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.provider.ProcessGroupProvider;
import systems.reformcloud.reformcloud2.executor.api.registry.io.FileRegistry;
import systems.reformcloud.reformcloud2.executor.api.utility.MoreCollections;
import systems.reformcloud.reformcloud2.node.cluster.ClusterManager;
import systems.reformcloud.reformcloud2.shared.group.DefaultProcessGroup;
import systems.reformcloud.reformcloud2.shared.registry.io.DefaultFileRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class DefaultNodeProcessGroupProvider implements ProcessGroupProvider {

  private final Collection<ProcessGroup> processGroups;
  private final FileRegistry fileRegistry;

  public DefaultNodeProcessGroupProvider(@NotNull String registryFolder) {
    this.fileRegistry = new DefaultFileRegistry(registryFolder, JsonConfiguration.DEFAULT_ADAPTER);
    this.processGroups = this.fileRegistry.readKeys(
      e -> e.get("key", DefaultProcessGroup.class),
      path -> System.err.println(TranslationHolder.translateDef("startup-unable-to-read-file",
        "Process-Group", path.toAbsolutePath().toString()))
    );
  }

  @NotNull
  @Override
  public Optional<ProcessGroup> getProcessGroup(@NotNull String name) {
    return Optional.ofNullable(MoreCollections.filter(this.processGroups, e -> e.getName().equals(name)));
  }

  @Override
  public void deleteProcessGroup(@NotNull String name) {
    ProcessGroup group = this.deleteProcessGroup0(name);
    if (group != null) {
      ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).publishProcessGroupDelete(group);
    }
  }

  @Override
  public void updateProcessGroup(@NotNull ProcessGroup processGroup) {
    this.updateProcessGroup0(processGroup);
    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).publishProcessGroupUpdate(processGroup);

    for (ProcessInformation processInformation : ExecutorAPI.getInstance().getProcessProvider().getProcessesByProcessGroup(processGroup.getName())) {
      processInformation.setProcessGroup(processGroup);
      ExecutorAPI.getInstance().getProcessProvider().updateProcessInformation(processInformation);
    }
  }

  @NotNull
  @Override
  public @UnmodifiableView Collection<ProcessGroup> getProcessGroups() {
    return Collections.unmodifiableCollection(this.processGroups);
  }

  @Override
  public long getProcessGroupCount() {
    return this.processGroups.size();
  }

  @NotNull
  @Override
  public @UnmodifiableView Collection<String> getProcessGroupNames() {
    return MoreCollections.map(this.processGroups, ProcessGroup::getName);
  }

  @NotNull
  @Override
  public ProcessGroupBuilder createProcessGroup(@NotNull String name) {
    return new NodeProcessGroupBuilder(this).name(name);
  }

  public void addProcessGroup(@NotNull ProcessGroup processGroup) {
    this.addProcessGroup0(processGroup);
    ExecutorAPI.getInstance().getServiceRegistry().getProvider(ClusterManager.class).ifPresent(e -> e.publishProcessGroupCreate(processGroup));
  }

  public void addProcessGroup0(@NotNull ProcessGroup processGroup) {
    this.processGroups.add(processGroup);
    this.fileRegistry.createKey(processGroup.getName(), processGroup);
  }

  public void updateProcessGroup0(@NotNull ProcessGroup processGroup) {
    this.getProcessGroup(processGroup.getName()).ifPresent(group -> {
      this.processGroups.remove(group);
      this.processGroups.add(processGroup);

      this.fileRegistry.updateKey(processGroup.getName(), processGroup);
    });
  }

  public @Nullable ProcessGroup deleteProcessGroup0(@NotNull String name) {
    Optional<ProcessGroup> processGroup = this.getProcessGroup(name);
    processGroup.ifPresent(group -> {
      this.processGroups.remove(group);
      this.fileRegistry.deleteKey(group.getName());
    });
    return processGroup.orElse(null);
  }

  public void reload() {
    this.processGroups.clear();
    this.processGroups.addAll(this.fileRegistry.readKeys(
      e -> e.get("key", DefaultProcessGroup.class),
      path -> System.err.println(TranslationHolder.translateDef("startup-unable-to-read-file",
        "Process-Group", path.toAbsolutePath().toString()))
    ));
  }
}
