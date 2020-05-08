package systems.reformcloud.reformcloud2.executor.api.common.groups;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.List;
import java.util.Objects;

public class ProcessGroup implements Nameable, SerializableObject {

    public static final TypeToken<ProcessGroup> TYPE = new TypeToken<ProcessGroup>() {
    };

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

    private String name;

    private boolean showIdInName;

    private StartupConfiguration startupConfiguration;

    private List<Template> templates;

    private PlayerAccessConfiguration playerAccessConfiguration;

    private boolean staticProcess;

    private boolean canBeUsedAsLobby;

    public boolean isShowIdInName() {
        return showIdInName;
    }

    @NotNull
    public StartupConfiguration getStartupConfiguration() {
        return startupConfiguration;
    }

    @NotNull
    public List<Template> getTemplates() {
        return templates;
    }

    @NotNull
    public PlayerAccessConfiguration getPlayerAccessConfiguration() {
        return playerAccessConfiguration;
    }

    public boolean isStaticProcess() {
        return staticProcess;
    }

    public void setStaticProcess(boolean staticProcess) {
        this.staticProcess = staticProcess;
    }

    public boolean isCanBeUsedAsLobby() {
        return canBeUsedAsLobby;
    }

    public void setCanBeUsedAsLobby(boolean canBeUsedAsLobby) {
        this.canBeUsedAsLobby = canBeUsedAsLobby;
    }

    public void setTemplates(List<Template> templates) {
        this.templates = templates;
    }

    @Nullable
    public Template getTemplate(@NotNull String name) {
        return Streams.filter(this.getTemplates(), e -> e.getName().equals(name));
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProcessGroup)) return false;
        ProcessGroup that = (ProcessGroup) o;
        return isShowIdInName() == that.isShowIdInName() &&
                isStaticProcess() == that.isStaticProcess() &&
                isCanBeUsedAsLobby() == that.isCanBeUsedAsLobby() &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getStartupConfiguration(), that.getStartupConfiguration()) &&
                Objects.equals(getTemplates(), that.getTemplates()) &&
                Objects.equals(getPlayerAccessConfiguration(), that.getPlayerAccessConfiguration());
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
