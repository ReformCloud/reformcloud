package systems.reformcloud.reformcloud2.executor.api.common.groups.basic;

import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.RuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.basic.FileBackend;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.inclusion.Inclusion;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.AutomaticStartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupEnvironment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DefaultProcessGroup extends ProcessGroup {

    public static final Inclusion PROXY_INCLUSION = new Inclusion(
            "reformcloud/global/proxies",
            FileBackend.NAME,
            Inclusion.InclusionLoadType.PAST
    );

    public static final Inclusion SERVER_INCLUSION = new Inclusion(
            "reformcloud/global/servers",
            FileBackend.NAME,
            Inclusion.InclusionLoadType.PAST
    );

    public DefaultProcessGroup(String name, int port, Version version, int maxMemory, boolean maintenance, int maxPlayers) {
        this(name, port, version, maxMemory, maintenance, maxPlayers, false, true);
    }

    public DefaultProcessGroup(String name, int port, Version version,
                               int maxMemory, boolean maintenance, int maxPlayers, boolean staticServer, boolean lobby) {
        super(
                name,
                true,
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
                        ),
                        version,
                        new ArrayList<>(),
                        Collections.singletonList(version.isServer() ? SERVER_INCLUSION : PROXY_INCLUSION)
                )), new PlayerAccessConfiguration(
                        "reformcloud.join.full",
                        maintenance,
                        "reformcloud.join.maintenance",
                        false,
                        null,
                        true,
                        true,
                        maxPlayers
                ), staticServer, lobby);
    }

    public DefaultProcessGroup(String name, int port, Version version,
                               int maxMemory, boolean maintenance, int min, int max, boolean staticServer, boolean lobby) {
        super(
                name,
                true,
                new StartupConfiguration(
                        max,
                        min,
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
                        ),
                        version,
                        new ArrayList<>(),
                        Collections.singletonList(version.isServer() ? SERVER_INCLUSION : PROXY_INCLUSION)
                )), new PlayerAccessConfiguration(
                        "reformcloud.join.full",
                        maintenance,
                        "reformcloud.join.maintenance",
                        false,
                        null,
                        true,
                        true,
                        50
                ), staticServer, lobby);
    }

    public DefaultProcessGroup(String name, int port, Version version,
                               int maxMemory, boolean maintenance, int min, int max, int prepared, int priority,
                               boolean staticServer, boolean lobby, List<String> clients, int maxPlayers) {
        super(
                name,
                true,
                new StartupConfiguration(
                        max,
                        min,
                        prepared,
                        priority,
                        port,
                        StartupEnvironment.JAVA_RUNTIME,
                        AutomaticStartupConfiguration.defaults(),
                        clients.isEmpty(),
                        clients
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
                        ),
                        version,
                        new ArrayList<>(),
                        Collections.singletonList(version.isServer() ? SERVER_INCLUSION : PROXY_INCLUSION)
                )), new PlayerAccessConfiguration(
                        "reformcloud.join.full",
                        maintenance,
                        "reformcloud.join.maintenance",
                        false,
                        null,
                        true,
                        true,
                        maxPlayers
                ), staticServer, lobby);
    }
}
