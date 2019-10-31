package systems.reformcloud.reformcloud2.executor.api.common.groups.basic;

import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.RuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.basic.FileBackend;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupEnvironment;

import java.util.Collections;

public final class DefaultProcessGroup extends ProcessGroup {

    public DefaultProcessGroup(String name, String parent, int port, Version version, int maxMemory, boolean maintenance, int maxPlayers) {
        this(name, parent, port, version, maxMemory, maintenance, maxPlayers, false, true);
    }

    public DefaultProcessGroup(String name, String parent, int port, Version version,
                               int maxMemory, boolean maintenance, int maxPlayers, boolean staticServer, boolean lobby) {
        super(
                name,
                true,
                parent,
                new StartupConfiguration(
                        -1,
                        1,
                        0,
                        port,
                        StartupEnvironment.JAVA_RUNTIME,
                        true,
                        Collections.emptyList()
                ), Collections.singletonList(new Template(
                        0,
                        "default",
                        false,
                        FileBackend.NAME,
                        "#",
                        new RuntimeConfiguration(
                                maxMemory,
                                Collections.emptyList(),
                                Collections.singletonMap("reformcloud2.developer", "derklaro")
                        ), version
                )), new PlayerAccessConfiguration(
                        maintenance,
                        "reformcloud2.join.maintenance",
                        false,
                        "reformcloud2.join.process",
                        true,
                        true,
                        true,
                        maxPlayers
                ), staticServer, lobby);
    }
}
