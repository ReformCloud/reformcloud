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
package systems.reformcloud.reformcloud2.proxy.defaults;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.proxy.ProxyConfiguration;
import systems.reformcloud.reformcloud2.proxy.ProxyConfigurationHandler;
import systems.reformcloud.reformcloud2.proxy.config.MotdConfiguration;
import systems.reformcloud.reformcloud2.proxy.config.TabListConfiguration;
import systems.reformcloud.reformcloud2.proxy.event.ProxyConfigurationUpdateEvent;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractProxyConfigurationHandler extends ProxyConfigurationHandler {

    private final AtomicInteger[] atomicIntegers = new AtomicInteger[]{
            new AtomicInteger(0),
            new AtomicInteger(0),
            new AtomicInteger(0)
    };
    private @Nullable ProxyConfiguration proxyConfiguration;
    private MotdConfiguration currentMessageOfTheDayConfiguration;
    private MotdConfiguration currentMaintenanceMessageOfTheDayConfiguration;
    private TabListConfiguration currentTabListConfiguration;

    @Override
    public @NotNull ProxyConfigurationHandler enable() {
        this.startTasks();
        return this;
    }

    @Override
    public @NotNull Optional<ProxyConfiguration> getProxyConfiguration() {
        return Optional.ofNullable(this.proxyConfiguration);
    }

    @Override
    public @NotNull Optional<TabListConfiguration> getCurrentTabListConfiguration() {
        return Optional.ofNullable(this.currentTabListConfiguration);
    }

    @Override
    public @NotNull Optional<MotdConfiguration> getCurrentMessageOfTheDayConfiguration() {
        return Optional.ofNullable(this.currentMessageOfTheDayConfiguration);
    }

    @Override
    public @NotNull Optional<MotdConfiguration> getCurrentMaintenanceMessageOfTheDayConfiguration() {
        return Optional.ofNullable(this.currentMaintenanceMessageOfTheDayConfiguration);
    }

    @Override
    public @NotNull Optional<MotdConfiguration> getBestMessageOfTheDayConfiguration() {
        return API.getInstance().getCurrentProcessInformation().getProcessGroup().getPlayerAccessConfiguration().isMaintenance()
                ? this.getCurrentMaintenanceMessageOfTheDayConfiguration()
                : this.getCurrentMessageOfTheDayConfiguration();
    }

    @Override
    public @NotNull String replaceMessageOfTheDayPlaceHolders(@NotNull String messageOfTheDay) {
        ProcessInformation current = API.getInstance().getCurrentProcessInformation();
        messageOfTheDay = messageOfTheDay
                .replace("%proxy_name%", current.getProcessDetail().getName())
                .replace("%proxy_display_name%", current.getProcessDetail().getDisplayName())
                .replace("%proxy_unique_id%", current.getProcessDetail().getProcessUniqueID().toString())
                .replace("%proxy_id%", Integer.toString(current.getProcessDetail().getId()))
                .replace("%proxy_online_players%", Integer.toString(current.getProcessPlayerManager().getOnlineCount()))
                .replace("%proxy_max_players%", Integer.toString(current.getProcessDetail().getMaxPlayers()))
                .replace("%proxy_group%", current.getProcessGroup().getName())
                .replace("%proxy_parent%", current.getProcessDetail().getParentName());
        return ProxyConfigurationHandler.translateAlternateColorCodes('&', messageOfTheDay);
    }

    @Override
    public @NotNull String replaceTabListPlaceHolders(@NotNull String tabList) {
        return this.replaceMessageOfTheDayPlaceHolders(tabList);
    }

    @Override
    public void handleProxyConfigUpdate(@NotNull ProxyConfiguration proxyConfiguration) {
        ExecutorAPI.getInstance().getEventManager().callEvent(new ProxyConfigurationUpdateEvent(this.proxyConfiguration = proxyConfiguration));
    }

    private void startTasks() {
        CommonHelper.EXECUTOR.execute(() -> {
            while (!Thread.interrupted()) {
                if (this.proxyConfiguration == null || this.proxyConfiguration.getMotdDefaultConfig().isEmpty()) {
                    AbsoluteThread.sleep(500);
                    continue;
                }

                int count = this.atomicIntegers[0].getAndIncrement();
                this.currentMessageOfTheDayConfiguration = this.proxyConfiguration.getMotdDefaultConfig().get(count);

                if (count >= this.proxyConfiguration.getMotdDefaultConfig().size() - 1) {
                    this.atomicIntegers[0].set(0);
                }

                AbsoluteThread.sleep(TimeUnit.SECONDS, this.currentMessageOfTheDayConfiguration.getWaitUntilNextInSeconds());
            }
        });

        CommonHelper.EXECUTOR.execute(() -> {
            while (!Thread.interrupted()) {
                if (this.proxyConfiguration == null || this.proxyConfiguration.getMotdMaintenanceConfig().isEmpty()) {
                    AbsoluteThread.sleep(500);
                    continue;
                }

                int count = this.atomicIntegers[1].getAndIncrement();
                this.currentMaintenanceMessageOfTheDayConfiguration = this.proxyConfiguration.getMotdMaintenanceConfig().get(count);

                if (count >= this.proxyConfiguration.getMotdMaintenanceConfig().size() - 1) {
                    this.atomicIntegers[1].set(0);
                }

                AbsoluteThread.sleep(TimeUnit.SECONDS, this.currentMaintenanceMessageOfTheDayConfiguration.getWaitUntilNextInSeconds());
            }
        });

        CommonHelper.EXECUTOR.execute(() -> {
            while (!Thread.interrupted()) {
                if (this.proxyConfiguration == null || this.proxyConfiguration.getTabListConfigurations().isEmpty()) {
                    AbsoluteThread.sleep(500);
                    continue;
                }

                int count = this.atomicIntegers[2].getAndIncrement();
                this.currentTabListConfiguration = this.proxyConfiguration.getTabListConfigurations().get(count);
                this.handleTabListChange();

                if (count >= this.proxyConfiguration.getTabListConfigurations().size() - 1) {
                    this.atomicIntegers[2].set(0);
                }

                AbsoluteThread.sleep(TimeUnit.SECONDS, this.currentTabListConfiguration.getWaitUntilNextInSeconds());
            }
        });
    }
}
