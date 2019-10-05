package systems.reformcloud.reformcloud2.executor.api.common.api.group;

import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.Template;

import java.util.List;
import java.util.function.Consumer;

public interface GroupSyncAPI {

    MainGroup createMainGroup(String name);

    MainGroup createMainGroup(String name, List<String> subgroups);

    ProcessGroup createProcessGroup(String name);

    ProcessGroup createProcessGroup(String name, String parent);

    ProcessGroup createProcessGroup(String name, String parent, List<Template> templates);

    ProcessGroup createProcessGroup(String name, String parent, List<Template> templates,
                                    StartupConfiguration startupConfiguration);

    ProcessGroup createProcessGroup(String name, String parent, List<Template> templates,
                                    StartupConfiguration startupConfiguration, PlayerAccessConfiguration playerAccessConfiguration);

    ProcessGroup createProcessGroup(String name, String parent, List<Template> templates,
                                    StartupConfiguration startupConfiguration, PlayerAccessConfiguration playerAccessConfiguration,
                                    boolean staticGroup);

    ProcessGroup createProcessGroup(ProcessGroup processGroup);

    MainGroup updateMainGroup(MainGroup mainGroup);

    ProcessGroup updateProcessGroup(ProcessGroup processGroup);

    MainGroup getMainGroup(String name);

    ProcessGroup getProcessGroup(String name);

    void deleteMainGroup(String name);

    void deleteProcessGroup(String name);

    List<MainGroup> getMainGroups();

    List<ProcessGroup> getProcessGroups();

    default void forEachProcessGroups(Consumer<ProcessGroup> action) {
        getProcessGroups().forEach(action);
    }

    default void forEachMainGroups(Consumer<MainGroup> action) {
        getMainGroups().forEach(action);
    }
}
