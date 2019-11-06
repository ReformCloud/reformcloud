package systems.reformcloud.reformcloud2.executor.api.common.groups;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class ProcessGroup implements Nameable {

    public static final TypeToken<ProcessGroup> TYPE = new TypeToken<ProcessGroup>() {};

    public ProcessGroup(String name, boolean showIdInName, String parentGroup,
                        StartupConfiguration startupConfiguration, List<Template> templates,
                        PlayerAccessConfiguration playerAccessConfiguration, boolean staticProcess) {
        this.name = name;
        this.showIdInName = showIdInName;
        this.parentGroup = parentGroup;
        this.startupConfiguration = startupConfiguration;
        this.templates = templates;
        this.playerAccessConfiguration = playerAccessConfiguration;
        this.staticProcess = staticProcess;
        this.canBeUsedAsLobby = false;
    }

    public ProcessGroup(String name, boolean showIdInName, String parentGroup,
                        StartupConfiguration startupConfiguration, List<Template> templates,
                        PlayerAccessConfiguration playerAccessConfiguration, boolean staticProcess, boolean asLobby) {
        this.name = name;
        this.showIdInName = showIdInName;
        this.parentGroup = parentGroup;
        this.startupConfiguration = startupConfiguration;
        this.templates = templates;
        this.playerAccessConfiguration = playerAccessConfiguration;
        this.staticProcess = staticProcess;
        this.canBeUsedAsLobby = asLobby;
    }

    private String name;

    private boolean showIdInName;

    private String parentGroup;

    private StartupConfiguration startupConfiguration;

    private List<Template> templates;

    private PlayerAccessConfiguration playerAccessConfiguration;

    private boolean staticProcess;

    private boolean canBeUsedAsLobby;

    public boolean isShowIdInName() {
        return showIdInName;
    }

    public String getParentGroup() {
        return parentGroup == null ? "null" : parentGroup;
    }

    public StartupConfiguration getStartupConfiguration() {
        return startupConfiguration;
    }

    public List<Template> getTemplates() {
        return templates;
    }

    public PlayerAccessConfiguration getPlayerAccessConfiguration() {
        return playerAccessConfiguration;
    }

    public boolean isStaticProcess() {
        return staticProcess;
    }

    public boolean isCanBeUsedAsLobby() {
        return canBeUsedAsLobby;
    }

    @Nonnull
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
                Objects.equals(getParentGroup(), that.getParentGroup()) &&
                Objects.equals(getStartupConfiguration(), that.getStartupConfiguration()) &&
                Objects.equals(getTemplates(), that.getTemplates()) &&
                Objects.equals(getPlayerAccessConfiguration(), that.getPlayerAccessConfiguration());
    }
}
