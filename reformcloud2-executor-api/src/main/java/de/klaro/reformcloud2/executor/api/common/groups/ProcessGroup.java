package de.klaro.reformcloud2.executor.api.common.groups;

import com.google.gson.reflect.TypeToken;
import de.klaro.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import de.klaro.reformcloud2.executor.api.common.groups.utils.StartupConfiguration;
import de.klaro.reformcloud2.executor.api.common.groups.utils.Template;
import de.klaro.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.List;

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

    @Override
    public String getName() {
        return name;
    }
}
