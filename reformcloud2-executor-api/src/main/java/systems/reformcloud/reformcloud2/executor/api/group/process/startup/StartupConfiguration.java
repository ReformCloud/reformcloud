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
package systems.reformcloud.reformcloud2.executor.api.group.process.startup;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import systems.reformcloud.reformcloud2.executor.api.network.data.SerializableObject;

import java.util.Collection;

public interface StartupConfiguration extends SerializableObject, Cloneable {

    @NotNull
    static StartupConfiguration newDefaultConfiguration() {
        return new DefaultStartupConfiguration(-1, 1, 1);
    }

    @NotNull
    static StartupConfiguration configuration(int maximumProcessAmount, int alwaysOnlineProcessAmount, int alwaysPreparedProcessAmount) {
        return new DefaultStartupConfiguration(maximumProcessAmount, alwaysOnlineProcessAmount, alwaysPreparedProcessAmount);
    }

    @NotNull
    static StartupConfiguration configuration(int maximumProcessAmount, int alwaysOnlineProcessAmount, int alwaysPreparedProcessAmount,
                                              @NotNull String jvmCommand, @NotNull AutomaticStartupConfiguration startupConfiguration, @NotNull Collection<String> startingNodes) {
        return new DefaultStartupConfiguration(maximumProcessAmount, alwaysOnlineProcessAmount, alwaysPreparedProcessAmount, jvmCommand, startupConfiguration, startingNodes);
    }

    @Range(from = -1, to = Integer.MAX_VALUE)
    int getMaximumProcessAmount();

    void setMaximumProcessAmount(@Range(from = -1, to = Integer.MAX_VALUE) int maximumProcessAmount);

    @Range(from = 0, to = Integer.MAX_VALUE)
    int getAlwaysOnlineProcessAmount();

    void setAlwaysOnlineProcessAmount(@Range(from = 0, to = Integer.MAX_VALUE) int alwaysOnlineProcessAmount);

    @Range(from = 0, to = Integer.MAX_VALUE)
    int getAlwaysPreparedProcessAmount();

    void setAlwaysPreparedProcessAmount(@Range(from = 0, to = Integer.MAX_VALUE) int alwaysPreparedProcessAmount);

    @NotNull
    String getJvmCommand();

    void setJvmCommand(@NotNull String jvmCommand);

    @NotNull
    AutomaticStartupConfiguration getAutomaticStartupConfiguration();

    void setAutomaticStartupConfiguration(@NotNull AutomaticStartupConfiguration automaticStartupConfiguration);

    @NotNull
    Collection<String> getStartingNodes();

    void setStartingNodes(@NotNull Collection<String> startingNodes);

    void addStartingNode(@NotNull String node);

    void removeStartingNode(@NotNull String node);

    @NotNull
    StartupConfiguration clone();
}
