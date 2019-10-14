package systems.reformcloud.reformcloud2.executor.api.common.api.group;

import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.util.List;

public interface GroupAsyncAPI extends GroupSyncAPI {

    Task<MainGroup> createMainGroupAsync(String name);

    Task<MainGroup> createMainGroupAsync(String name, List<String> subgroups);

    Task<ProcessGroup> createProcessGroupAsync(String name);

    Task<ProcessGroup> createProcessGroupAsync(String name, String parent);

    Task<ProcessGroup> createProcessGroupAsync(String name, String parent, List<Template> templates);

    Task<ProcessGroup> createProcessGroupAsync(String name, String parent, List<Template> templates,
                                    StartupConfiguration startupConfiguration);

    Task<ProcessGroup> createProcessGroupAsync(String name, String parent, List<Template> templates,
                                    StartupConfiguration startupConfiguration, PlayerAccessConfiguration playerAccessConfiguration);

    Task<ProcessGroup> createProcessGroupAsync(String name, String parent, List<Template> templates,
                                    StartupConfiguration startupConfiguration, PlayerAccessConfiguration playerAccessConfiguration,
                                    boolean staticGroup);

    Task<ProcessGroup> createProcessGroupAsync(ProcessGroup processGroup);

    Task<MainGroup> updateMainGroupAsync(MainGroup mainGroup);

    Task<ProcessGroup> updateProcessGroupAsync(ProcessGroup processGroup);

    Task<MainGroup> getMainGroupAsync(String name);

    Task<ProcessGroup> getProcessGroupAsync(String name);

    Task<Void> deleteMainGroupAsync(String name);

    Task<Void> deleteProcessGroupAsync(String name);

    Task<List<MainGroup>> getMainGroupsAsync();

    Task<List<ProcessGroup>> getProcessGroupsAsync();
}
