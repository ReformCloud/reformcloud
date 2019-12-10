package systems.reformcloud.reformcloud2.executor.api.common;

import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.common.api.AsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.SyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class ExecutorAPI {

    protected ExecutorType type;

    /* ========================== */

    private static ExecutorAPI instance;

    protected static void setInstance(@Nonnull ExecutorAPI instance) {
        Conditions.isTrue(ExecutorAPI.instance == null, "Executor api instance is already defined");
        ExecutorAPI.instance = Objects.requireNonNull(instance, "instance");
    }

    @Nonnull
    public static ExecutorAPI getInstance() {
        return instance;
    }

    /* ========================== */

    @Nonnull
    public abstract SyncAPI getSyncAPI();

    @Nonnull
    public abstract AsyncAPI getAsyncAPI();

    /* ========================== */

    @Nonnull
    public abstract PacketHandler getPacketHandler();

    @Nonnull
    public abstract EventManager getEventManager();

    /* ========================== */

    public abstract boolean isReady();

    /* ========================== */

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
