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
package systems.reformcloud.reformcloud2.executor.api.common.groups.template;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.inclusion.Inclusion;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Duo;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public final class Template implements Nameable, SerializableObject {

    public static final TypeToken<Template> TYPE = new TypeToken<Template>() {
    };
    private int priority;
    private String name;
    private boolean global;
    private boolean autoReleaseOnClose;
    private String backend;
    private String serverNameSplitter;
    private RuntimeConfiguration runtimeConfiguration;
    private Version version;
    private Collection<Inclusion> templateInclusions;
    private Collection<Inclusion> pathInclusions;

    @ApiStatus.Internal
    public Template() {
    }

    public Template(int priority, String name, boolean global, String backend, String serverNameSplitter,
                    RuntimeConfiguration runtimeConfiguration, Version version) {
        this(priority, name, global, backend, serverNameSplitter, runtimeConfiguration, version, new ArrayList<>(), new ArrayList<>());
    }

    public Template(int priority, String name, boolean global, String backend, String serverNameSplitter,
                    RuntimeConfiguration runtimeConfiguration, Version version, Collection<Inclusion> templateInclusions,
                    Collection<Inclusion> pathInclusions) {
        this(priority, name, global, false, backend, serverNameSplitter, runtimeConfiguration, version, templateInclusions, pathInclusions);
    }

    public Template(int priority, String name, boolean global, boolean autoReleaseOnClose, String backend, String serverNameSplitter,
                    RuntimeConfiguration runtimeConfiguration, Version version, Collection<Inclusion> templateInclusions,
                    Collection<Inclusion> pathInclusions) {
        this.priority = priority;
        this.name = name;
        this.global = global;
        this.autoReleaseOnClose = autoReleaseOnClose;
        this.backend = backend;
        this.serverNameSplitter = serverNameSplitter;
        this.runtimeConfiguration = runtimeConfiguration;
        this.version = version;
        this.templateInclusions = templateInclusions;
        this.pathInclusions = pathInclusions;
    }

    public int getPriority() {
        return this.priority;
    }

    @NotNull
    @Override
    public String getName() {
        return this.name;
    }

    public boolean isGlobal() {
        return this.global;
    }

    public boolean isAutoReleaseOnClose() {
        return this.autoReleaseOnClose;
    }

    @NotNull
    public String getBackend() {
        return this.backend;
    }

    @Nullable
    public String getServerNameSplitter() {
        return this.serverNameSplitter;
    }

    @NotNull
    public RuntimeConfiguration getRuntimeConfiguration() {
        return this.runtimeConfiguration;
    }

    @NotNull
    public Version getVersion() {
        return this.version;
    }

    public boolean isServer() {
        return this.version.isServer();
    }

    /* Needs null check, added in version 2.0.4 */
    public Collection<Inclusion> getTemplateInclusions() {
        return this.templateInclusions == null ? new ArrayList<>() : this.templateInclusions;
    }

    /* Needs null check, added in version 2.0.4 */
    public Collection<Inclusion> getPathInclusions() {
        return this.pathInclusions == null ? new ArrayList<>() : this.pathInclusions;
    }

    public Collection<Duo<String, String>> getPathInclusionsOfType(@NotNull Inclusion.InclusionLoadType type) {
        return this.getPathInclusions()
                .stream()
                .filter(e -> e.getInclusionLoadType().equals(type))
                .filter(e -> e.getBackend() != null && e.getKey() != null)
                .map(e -> new Duo<>(e.getKey(), e.getBackend()))
                .collect(Collectors.toList());
    }

    public Collection<Duo<String, String>> getTemplateInclusionsOfType(@NotNull Inclusion.InclusionLoadType type) {
        return this.getTemplateInclusions()
                .stream()
                .filter(e -> e.getInclusionLoadType().equals(type))
                .filter(e -> e.getBackend() != null && e.getKey() != null)
                .map(e -> new Duo<>(e.getKey(), e.getBackend()))
                .collect(Collectors.toList());
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeVarInt(this.priority);
        buffer.writeString(this.name);
        buffer.writeBoolean(this.global);
        buffer.writeBoolean(this.autoReleaseOnClose);
        buffer.writeString(this.backend);
        buffer.writeString(this.serverNameSplitter);
        buffer.writeObject(this.runtimeConfiguration);
        buffer.writeVarInt(this.version.ordinal());
        buffer.writeObjects(this.templateInclusions);
        buffer.writeObjects(this.pathInclusions);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.priority = buffer.readVarInt();
        this.name = buffer.readString();
        this.global = buffer.readBoolean();
        this.autoReleaseOnClose = buffer.readBoolean();
        this.backend = buffer.readString();
        this.serverNameSplitter = buffer.readString();
        this.runtimeConfiguration = buffer.readObject(RuntimeConfiguration.class);
        this.version = Version.values()[buffer.readVarInt()];
        this.templateInclusions = buffer.readObjects(Inclusion.class);
        this.pathInclusions = buffer.readObjects(Inclusion.class);
    }
}
