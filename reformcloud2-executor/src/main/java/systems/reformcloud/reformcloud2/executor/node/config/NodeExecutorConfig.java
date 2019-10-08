package systems.reformcloud.reformcloud2.executor.node.config;

import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.registry.Registry;
import systems.reformcloud.reformcloud2.executor.api.common.registry.basic.RegistryBuilder;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.ClusterFirewall;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import static systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links.newCollection;

public class NodeExecutorConfig {

    private static final Collection<Path> PATHS = newCollection(
            s -> Paths.get(s),
            "reformcloud/groups/main",
            "reformcloud/groups/sub",
            "reformcloud/configs",
            "reformcloud/applications"
    );

    private ClusterFirewall clusterFirewall;

    private NodeInformation self;

    private final Registry localMainGroupsRegistry = RegistryBuilder.newRegistry(Paths.get("reformcloud/groups/main"));

    private final Registry localSubGroupsRegistry = RegistryBuilder.newRegistry(Paths.get("reformcloud/groups/sub"));

    public void init() {

    }

    public NodeInformation getSelf() {
        return self;
    }

    public ClusterFirewall getClusterFirewall() {
        return clusterFirewall;
    }
}
