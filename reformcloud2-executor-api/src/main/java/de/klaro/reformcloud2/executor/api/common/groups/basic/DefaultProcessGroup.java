package de.klaro.reformcloud2.executor.api.common.groups.basic;

import de.klaro.reformcloud2.executor.api.common.groups.ProcessGroup;
import de.klaro.reformcloud2.executor.api.common.groups.utils.*;

import java.util.Collections;

public final class DefaultProcessGroup extends ProcessGroup {
    public DefaultProcessGroup(String name, String parent, int port, Version version, int maxMemory, boolean maintenance, int maxPlayers) {
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
                        "#",
                        null,
                        new RuntimeConfiguration(
                                maxMemory,
                                Collections.emptyList(),
                                Collections.singletonMap("reformcloud2.developer", "_Klaro")
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
                ), false);
    }
}
