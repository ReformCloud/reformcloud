package de.klaro.reformcloud2.executor.api.common;

import de.klaro.reformcloud2.executor.api.ExecutorType;
import de.klaro.reformcloud2.executor.api.common.api.applications.ApplicationAsyncAPI;
import de.klaro.reformcloud2.executor.api.common.api.client.ClientAsyncAPI;
import de.klaro.reformcloud2.executor.api.common.api.console.ConsoleAsyncAPI;
import de.klaro.reformcloud2.executor.api.common.api.database.DatabaseAsyncAPI;
import de.klaro.reformcloud2.executor.api.common.api.group.GroupAsyncAPI;
import de.klaro.reformcloud2.executor.api.common.api.player.PlayerAsyncAPI;
import de.klaro.reformcloud2.executor.api.common.api.plugins.PluginAsyncAPI;
import de.klaro.reformcloud2.executor.api.common.api.process.ProcessAsyncAPI;
import de.klaro.reformcloud2.executor.api.common.base.Conditions;

import java.util.Objects;

public abstract class ExecutorAPI implements
        ProcessAsyncAPI,
        GroupAsyncAPI,
        ApplicationAsyncAPI,
        ConsoleAsyncAPI,
        PlayerAsyncAPI,
        PluginAsyncAPI,
        ClientAsyncAPI,
        DatabaseAsyncAPI {

    protected ExecutorType type;

    /* ========================== */

    private static ExecutorAPI instance;

    protected static void setInstance(ExecutorAPI instance) {
        Conditions.isTrue(ExecutorAPI.instance == null, "Executor api instance is already defined");
        ExecutorAPI.instance = Objects.requireNonNull(instance);
    }

    public static ExecutorAPI getInstance() {
        return instance;
    }

    /* ========================== */

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
