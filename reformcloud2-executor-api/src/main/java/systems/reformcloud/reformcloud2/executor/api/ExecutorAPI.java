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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.api.AsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.api.SyncAPI;
import systems.reformcloud.reformcloud2.executor.api.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.network.packet.handler.PacketHandler;

import java.util.Objects;

/**
 * This class represents the whole api of the cloud system
 */
public abstract class ExecutorAPI {

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("io.netty.noPreferDirect", "true");
        System.setProperty("io.netty.maxDirectMemory", "0");
        System.setProperty("io.netty.leakDetectionLevel", "DISABLED");
        System.setProperty("io.netty.recycler.maxCapacity", "0");
        System.setProperty("io.netty.recycler.maxCapacity.default", "0");
        System.setProperty("io.netty.allocator.type", "UNPOOLED");

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
        Conditions.isTrue(ExecutorAPI.instance == null, "Executor api instance is already defined");
        ExecutorAPI.instance = Objects.requireNonNull(instance, "instance");
    }

    /**
     * @return The current sync api instance of the api
     */
    @NotNull
    public abstract SyncAPI getSyncAPI();

    /**
     * @return The current async api instance
     */
    @NotNull
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public abstract AsyncAPI getAsyncAPI();

    /**
     * @return The current packet handler of the cloud
     */
    @NotNull
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public abstract PacketHandler getPacketHandler();

    /**
     * @return The current event manger of the cloud
     */
    @NotNull
    public abstract EventManager getEventManager();

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
