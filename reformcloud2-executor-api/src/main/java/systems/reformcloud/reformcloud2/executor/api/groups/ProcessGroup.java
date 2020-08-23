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
package systems.reformcloud.reformcloud2.executor.api.groups;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.utils.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.utility.name.Nameable;

import java.util.List;
import java.util.Objects;

public class ProcessGroup implements Nameable, SerializableObject {

    public static final TypeToken<ProcessGroup> TYPE = new TypeToken<ProcessGroup>() {
    };
    private String name;
    private boolean showIdInName;
    private StartupConfiguration startupConfiguration;
    private List<Template> templates;
    private PlayerAccessConfiguration playerAccessConfiguration;
    private boolean staticProcess;
    private boolean canBeUsedAsLobby;

    @ApiStatus.Internal
    public ProcessGroup() {
    }

    public ProcessGroup(String name, boolean showIdInName,
                        StartupConfiguration startupConfiguration, List<Template> templates,
                        PlayerAccessConfiguration playerAccessConfiguration, boolean staticProcess) {
        this.name = name;
        this.showIdInName = showIdInName;
        this.startupConfiguration = startupConfiguration;
        this.templates = templates;
        this.playerAccessConfiguration = playerAccessConfiguration;
        this.staticProcess = staticProcess;
        this.canBeUsedAsLobby = false;
    }

    public ProcessGroup(String name, boolean showIdInName,
                        StartupConfiguration startupConfiguration, List<Template> templates,
                        PlayerAccessConfiguration playerAccessConfiguration, boolean staticProcess, boolean asLobby) {
        this.name = name;
        this.showIdInName = showIdInName;
        this.startupConfiguration = startupConfiguration;
        this.templates = templates;
        this.playerAccessConfiguration = playerAccessConfiguration;
        this.staticProcess = staticProcess;
        this.canBeUsedAsLobby = asLobby;
    }

    public boolean isShowIdInName() {
        return this.showIdInName;
    }

    @NotNull
    public StartupConfiguration getStartupConfiguration() {
        return this.startupConfiguration;
    }

    @NotNull
    public List<Template> getTemplates() {
        return this.templates;
    }

    public void setTemplates(List<Template> templates) {
        this.templates = templates;
    }

    @NotNull
    public PlayerAccessConfiguration getPlayerAccessConfiguration() {
        return this.playerAccessConfiguration;
    }

    public boolean isStaticProcess() {
        return this.staticProcess;
    }

    public void setStaticProcess(boolean staticProcess) {
        this.staticProcess = staticProcess;
    }

    public boolean isCanBeUsedAsLobby() {
        return this.canBeUsedAsLobby;
    }

    public void setCanBeUsedAsLobby(boolean canBeUsedAsLobby) {
        this.canBeUsedAsLobby = canBeUsedAsLobby;
    }

    @Nullable
    public Template getTemplate(@NotNull String name) {
        return Streams.filter(this.getTemplates(), e -> e.getName().equals(name));
    }

    @NotNull
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProcessGroup)) return false;
        ProcessGroup that = (ProcessGroup) o;
        return this.isShowIdInName() == that.isShowIdInName() &&
                this.isStaticProcess() == that.isStaticProcess() &&
                this.isCanBeUsedAsLobby() == that.isCanBeUsedAsLobby() &&
                Objects.equals(this.getName(), that.getName()) &&
                Objects.equals(this.getStartupConfiguration(), that.getStartupConfiguration()) &&
                Objects.equals(this.getTemplates(), that.getTemplates()) &&
                Objects.equals(this.getPlayerAccessConfiguration(), that.getPlayerAccessConfiguration());
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.name);
        buffer.writeBoolean(this.showIdInName);
        buffer.writeObject(this.startupConfiguration);
        buffer.writeObjects(this.templates);
        buffer.writeObject(this.playerAccessConfiguration);
        buffer.writeBoolean(this.staticProcess);
        buffer.writeBoolean(this.canBeUsedAsLobby);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.name = buffer.readString();
        this.showIdInName = buffer.readBoolean();
        this.startupConfiguration = buffer.readObject(StartupConfiguration.class);
        this.templates = buffer.readObjects(Template.class);
        this.playerAccessConfiguration = buffer.readObject(PlayerAccessConfiguration.class);
        this.staticProcess = buffer.readBoolean();
        this.canBeUsedAsLobby = buffer.readBoolean();
    }
}
