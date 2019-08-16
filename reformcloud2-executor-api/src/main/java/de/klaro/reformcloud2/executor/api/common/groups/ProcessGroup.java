package de.klaro.reformcloud2.executor.api.common.groups;

import de.klaro.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import de.klaro.reformcloud2.executor.api.common.groups.utils.StartupConfiguration;
import de.klaro.reformcloud2.executor.api.common.groups.utils.Template;
import de.klaro.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.Map;

public class ProcessGroup implements Nameable {

    public ProcessGroup(String name, String parentGroup, StartupConfiguration startupConfiguration,
                        Map<Integer, Template> templatePerPriority,
                        PlayerAccessConfiguration playerAccessConfiguration, boolean staticProcess) {
        this.name = name;
        this.parentGroup = parentGroup;
        this.startupConfiguration = startupConfiguration;
        this.templatePerPriority = templatePerPriority;
        this.playerAccessConfiguration = playerAccessConfiguration;
        this.staticProcess = staticProcess;
    }

    private String name;

    private String parentGroup;

    private StartupConfiguration startupConfiguration;

    private Map<Integer, Template> templatePerPriority;

    private PlayerAccessConfiguration playerAccessConfiguration;

    private boolean staticProcess;

    public String getParentGroup() {
        return parentGroup;
    }

    public StartupConfiguration getStartupConfiguration() {
        return startupConfiguration;
    }

    public Map<Integer, Template> getTemplatePerPriority() {
        return templatePerPriority;
    }

    public PlayerAccessConfiguration getPlayerAccessConfiguration() {
        return playerAccessConfiguration;
    }

    public boolean isStaticProcess() {
        return staticProcess;
    }

    @Override
    public String getName() {
        return name;
    }
}
