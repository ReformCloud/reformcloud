package systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util;

public enum ReceiverType {

    /**
     * The receivers are the connected proxies
     */
    PROXY,

    /**
     * The receivers are the connected minecraft servers
     */
    SERVER,

    /**
     * The receivers are the other nodes/clients in the network
     */
    OTHERS
}
