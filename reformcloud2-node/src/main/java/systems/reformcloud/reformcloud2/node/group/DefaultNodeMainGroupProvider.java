/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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
import systems.reformcloud.reformcloud2.executor.api.builder.MainGroupBuilder;
import systems.reformcloud.reformcloud2.executor.api.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.provider.MainGroupProvider;
import systems.reformcloud.reformcloud2.executor.api.registry.io.FileRegistry;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;
import systems.reformcloud.reformcloud2.node.cluster.ClusterManager;
import systems.reformcloud.reformcloud2.shared.registry.io.DefaultFileRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class DefaultNodeMainGroupProvider implements MainGroupProvider {

    public DefaultNodeMainGroupProvider(@NotNull String registryFolder) {
        this.fileRegistry = new DefaultFileRegistry(registryFolder);
        this.mainGroups = this.fileRegistry.readKeys(
                e -> e.get("key", MainGroup.TYPE),
                path -> System.err.println(LanguageManager.get("startup-unable-to-read-file",
                        "Main-Group", path.toAbsolutePath().toString()))
        );
    }

    private final Collection<MainGroup> mainGroups;
    private final FileRegistry fileRegistry;

    @NotNull
    @Override
    public Optional<MainGroup> getMainGroup(@NotNull String name) {
        return Optional.ofNullable(Streams.filter(this.mainGroups, e -> e.getName().equals(name)));
    }

    @Override
    public void deleteMainGroup(@NotNull String name) {
        MainGroup mainGroup = this.deleteMainGroup0(name);
        if (mainGroup == null) {
            return;
        }

        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).publishMainGroupDelete(mainGroup);
    }

    @Override
    public void updateMainGroup(@NotNull MainGroup mainGroup) {
        this.updateMainGroup0(mainGroup);
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).publishMainGroupUpdate(mainGroup);
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<MainGroup> getMainGroups() {
        return Collections.unmodifiableCollection(this.mainGroups);
    }

    @Override
    public long getMainGroupCount() {
        return this.mainGroups.size();
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<String> getMainGroupNames() {
        return Streams.map(this.mainGroups, MainGroup::getName);
    }

    @NotNull
    @Override
    public MainGroupBuilder createMainGroup(@NotNull String name) {
        return new NodeMainGroupBuilder(this).name(name);
    }

    public void addGroup(@NotNull MainGroup mainGroup) {
        this.addGroup0(mainGroup);
        ExecutorAPI.getInstance().getServiceRegistry().getProvider(ClusterManager.class).ifPresent(e -> e.publishMainGroupCreate(mainGroup));
    }

    public void addGroup0(@NotNull MainGroup mainGroup) {
        this.mainGroups.add(mainGroup);
        this.fileRegistry.createKey(mainGroup.getName(), mainGroup);
    }

    public @Nullable MainGroup deleteMainGroup0(@NotNull String name) {
        Optional<MainGroup> mainGroup = this.getMainGroup(name);
        mainGroup.ifPresent(group -> {
            this.fileRegistry.deleteKey(group.getName());
            this.mainGroups.remove(group);
        });
        return mainGroup.orElse(null);
    }

    public void updateMainGroup0(@NotNull MainGroup mainGroup) {
        this.getMainGroup(mainGroup.getName()).ifPresent(group -> {
            this.mainGroups.remove(group);
            this.mainGroups.add(mainGroup);

            this.fileRegistry.updateKey(mainGroup.getName(), mainGroup);
        });
    }

    public void reload() {
        this.mainGroups.clear();
        this.mainGroups.addAll(this.fileRegistry.readKeys(
                e -> e.get("key", MainGroup.TYPE),
                path -> System.err.println(LanguageManager.get("startup-unable-to-read-file",
                        "Main-Group", path.toAbsolutePath().toString()))
        ));
    }
}
