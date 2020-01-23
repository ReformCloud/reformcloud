package systems.reformcloud.reformcloud2.executor.api.common;

import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.common.api.AsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.SyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * This class represents the whole api of the cloud system
 */
public abstract class ExecutorAPI {

    /* The executor type which is currently running */
    protected ExecutorType type = ExecutorType.UNKNOWN;

    /* ========================== */

    /* The current instance of the executor */
    private static ExecutorAPI instance;

    /**
     * Updates the current instance of the cloud system
     * <br>
     * This action can only performed once on the current instance, normally by the cloud itself
     *
     * @param instance The new instance of the executor api
     */
    protected static void setInstance(@Nonnull ExecutorAPI instance) {
        Conditions.isTrue(ExecutorAPI.instance == null, "Executor api instance is already defined");
        ExecutorAPI.instance = Objects.requireNonNull(instance, "instance");
    }

    /**
     * @return The current instance of this class
     */
    @Nonnull
    public static ExecutorAPI getInstance() {
        return instance;
    }

    /* ========================== */

    /**
     * @return The current sync api instance of the api
     */
    @Nonnull
    public abstract SyncAPI getSyncAPI();

    /**
     * @return The current async api instance
     */
    @Nonnull
    public abstract AsyncAPI getAsyncAPI();

    /* ========================== */

    /**
     * @return The current packet handler of the cloud
     */
    @Nonnull
    public abstract PacketHandler getPacketHandler();

    /**
     * @return The current event manger of the cloud
     */
    @Nonnull
    public abstract EventManager getEventManager();

    /* ========================== */

    /**
     * @return If the current cloud instance is ready
     */
    public abstract boolean isReady();

    /* ========================== */

    /**
     * @return The current type which the cloud is executing
     */
    @Nonnull
    public ExecutorType getType() {
        return type;
    }

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("io.netty.noPreferDirect", "true");
        System.setProperty("io.netty.maxDirectMemory", "0");
        System.setProperty("io.netty.leakDetectionLevel", "DISABLED");
        System.setProperty("io.netty.recycler.maxCapacity", "0");
        System.setProperty("io.netty.recycler.maxCapacity.default", "0");
        System.setProperty("io.netty.allocator.type", "UNPOOLED");
    }
}
