/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.api;

import io.netty.util.ResourceLeakDetector;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.provider.*;
import systems.reformcloud.reformcloud2.executor.api.registry.service.ServiceRegistry;

import java.util.Objects;

/**
 * This class represents the whole api of the cloud system
 */
public abstract class ExecutorAPI {

    static {
        if (System.getProperty("io.netty.leakDetectionLevel") == null) {
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
        }

        CommonHelper.setSystemPropertyIfUnset("io.netty.allocator.maxOrder", "9");
        CommonHelper.setSystemPropertyIfUnset("io.netty.noPreferDirect", "true");
        CommonHelper.setSystemPropertyIfUnset("io.netty.maxDirectMemory", "0");
        CommonHelper.setSystemPropertyIfUnset("io.netty.recycler.maxCapacity", "0");
        CommonHelper.setSystemPropertyIfUnset("io.netty.recycler.maxCapacity.default", "0");
        CommonHelper.setSystemPropertyIfUnset("io.netty.selectorAutoRebuildThreshold", "0");
        CommonHelper.setSystemPropertyIfUnset("io.netty.allocator.type", "UNPOOLED");

        Thread.setDefaultUncaughtExceptionHandler((t, ex) -> ex.printStackTrace());
    }

    /* The executor type which is currently running */
    protected ExecutorType type = ExecutorType.UNKNOWN;

    /* The current instance of the executor */
    private static ExecutorAPI instance;

    /**
     * @return The current instance of this class
     */
    @NotNull
    public static ExecutorAPI getInstance() {
        return instance;
    }

    /**
     * Updates the current instance of the cloud system
     * <br>
     * This action can only performed once on the current instance, normally by the cloud itself
     *
     * @param instance The new instance of the executor api
     */
    protected static void setInstance(@NotNull ExecutorAPI instance) {
        ExecutorAPI.instance = Objects.requireNonNull(instance, "instance");
    }

    /**
     * @return The current instance of the executor specific implemented version of the channel message provider
     */
    @NotNull
    public abstract ChannelMessageProvider getChannelMessageProvider();

    /**
     * @return The current instance of the executor specific implemented version of the database provider
     */
    @NotNull
    public abstract DatabaseProvider getDatabaseProvider();

    /**
     * @return The current instance of the executor specific implemented version of the main group provider
     */
    @NotNull
    public abstract MainGroupProvider getMainGroupProvider();

    /**
     * @return The current instance of the executor specific implemented version of the node information provider
     */
    @NotNull
    public abstract NodeInformationProvider getNodeInformationProvider();

    /**
     * @return The current instance of the executor specific implemented version of the player provider
     */
    @NotNull
    public abstract PlayerProvider getPlayerProvider();

    /**
     * @return The current instance of the executor specific implemented version of the process group provider
     */
    @NotNull
    public abstract ProcessGroupProvider getProcessGroupProvider();

    /**
     * @return The current instance of the executor specific implemented version of the process provider
     */
    @NotNull
    public abstract ProcessProvider getProcessProvider();

    @NotNull
    public abstract ServiceRegistry getServiceRegistry();

    /**
     * @return If the current cloud instance is ready
     */
    public abstract boolean isReady();

    /**
     * @return The current type which the cloud is executing
     */
    @NotNull
    public ExecutorType getType() {
        return this.type;
    }
}
