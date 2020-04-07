package systems.reformcloud.reformcloud2.executor.api.node.cluster;

/**
 * Represents all synchronisation actions a node can execute
 */
public enum SyncAction {

    /**
     * It's general sync and all information sent with should get set
     */
    SYNC,

    /**
     * A new information is available and should also get created in the other nodes
     */
    CREATE,

    /**
     * An existing information got updated and should get updated in all other nodes, too
     */
    UPDATE,

    /**
     * An existing information got deleted and should get deleted in all other nodes, too
     */
    DELETE
}
