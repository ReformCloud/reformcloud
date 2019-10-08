package systems.reformcloud.reformcloud2.executor.api.node.cluster;

public interface ClusterFirewall {

    boolean isIPAddressWhitelisted(String address);
}
