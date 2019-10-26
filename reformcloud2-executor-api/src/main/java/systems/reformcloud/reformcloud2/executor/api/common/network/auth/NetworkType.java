package systems.reformcloud.reformcloud2.executor.api.common.network.auth;

public enum NetworkType {

    /**
     * The current auth component is a process (Process -> Controller auth)
     */
    PROCESS,

    /**
     * The current auth component is a client (Client -> Controller auth)
     */
    CLIENT,

    /**
     * The current auth component is a node (Node -> Node auth)
     */
    NODE
}
