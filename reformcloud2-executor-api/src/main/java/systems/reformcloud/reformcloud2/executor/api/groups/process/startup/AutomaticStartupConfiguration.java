/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.reformcloud2.executor.api.groups.process.startup;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import systems.reformcloud.reformcloud2.executor.api.network.data.SerializableObject;

import java.util.concurrent.TimeUnit;

public interface AutomaticStartupConfiguration extends SerializableObject, Cloneable {

    @NotNull
    @Contract(pure = true)
    static AutomaticStartupConfiguration enabled() {
        return new DefaultAutomaticStartupConfiguration(true, true);
    }

    @NotNull
    @Contract(pure = true)
    static AutomaticStartupConfiguration disabled() {
        return new DefaultAutomaticStartupConfiguration(false, false);
    }

    @NotNull
    @Contract(pure = true)
    static AutomaticStartupConfiguration onlyAutomaticStartup() {
        return new DefaultAutomaticStartupConfiguration(true, false);
    }

    @NotNull
    @Contract(pure = true)
    static AutomaticStartupConfiguration onlyAutomaticShutdown() {
        return new DefaultAutomaticStartupConfiguration(false, true);
    }

    boolean isAutomaticStartupEnabled();

    void setAutomaticStartupEnabled(boolean automaticStartupEnabled);

    boolean isAutomaticShutdownEnabled();

    void setAutomaticShutdownEnabled(boolean automaticShutdownEnabled);

    @Range(from = 0, to = 100)
    int getMaxPercentOfPlayersToStart();

    void setMaxPercentOfPlayersToStart(@Range(from = 0, to = 100) int maxPercentOfPlayersToStart);

    @Range(from = 0, to = 100)
    int getMaxPercentOfPlayersToStop();

    void setMaxPercentOfPlayersToStop(@Range(from = 0, to = 100) int maxPercentOfPlayersToStop);

    @Range(from = 0, to = Long.MAX_VALUE)
    long getCheckIntervalInSeconds();

    void setCheckIntervalInSeconds(@Range(from = 0, to = Long.MAX_VALUE) long checkIntervalInSeconds);

    void setCheckInterval(@NotNull TimeUnit timeUnit, @Range(from = 0, to = Long.MAX_VALUE) long checkInterval);

    @NotNull
    AutomaticStartupConfiguration clone();
}
