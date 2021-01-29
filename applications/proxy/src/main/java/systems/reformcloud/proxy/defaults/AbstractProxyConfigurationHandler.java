/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
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
package systems.reformcloud.proxy.defaults;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.refomcloud.embedded.Embedded;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.event.EventManager;
import systems.reformcloud.process.ProcessInformation;
import systems.reformcloud.proxy.ProxyConfiguration;
import systems.reformcloud.proxy.ProxyConfigurationHandler;
import systems.reformcloud.proxy.config.MotdConfiguration;
import systems.reformcloud.proxy.config.TabListConfiguration;
import systems.reformcloud.proxy.event.ProxyConfigurationUpdateEvent;
import systems.reformcloud.shared.Constants;

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
    return Embedded.getInstance().getCurrentProcessInformation().getProcessGroup().getPlayerAccessConfiguration().isMaintenance()
      ? this.getCurrentMaintenanceMessageOfTheDayConfiguration()
      : this.getCurrentMessageOfTheDayConfiguration();
  }

  @Override
  public @NotNull String replaceMessageOfTheDayPlaceHolders(@NotNull String messageOfTheDay) {
    ProcessInformation current = Embedded.getInstance().getCurrentProcessInformation();
    messageOfTheDay = messageOfTheDay
      .replace("%proxy_name%", current.getName())
      .replace("%proxy_display_name%", current.getId().getDisplayName())
      .replace("%proxy_unique_id%", current.getId().getUniqueId().toString())
      .replace("%proxy_id%", Integer.toString(current.getId().getId()))
      .replace("%proxy_online_players%", Integer.toString(current.getOnlineCount()))
      .replace("%proxy_max_players%", Integer.toString(current.getProcessGroup().getPlayerAccessConfiguration().getMaxPlayers()))
      .replace("%proxy_group%", current.getProcessGroup().getName())
      .replace("%proxy_parent%", current.getId().getNodeName());
    return ProxyConfigurationHandler.translateAlternateColorCodes('&', messageOfTheDay);
  }

  @Override
  public @NotNull String replaceTabListPlaceHolders(@NotNull String tabList) {
    return this.replaceMessageOfTheDayPlaceHolders(tabList);
  }

  @Override
  public void handleProxyConfigUpdate(@NotNull ProxyConfiguration proxyConfiguration) {
    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new ProxyConfigurationUpdateEvent(this.proxyConfiguration = proxyConfiguration));
  }

  private void startTasks() {
    Constants.CACHED_THREAD_POOL.execute(() -> {
      while (!Thread.interrupted()) {
        if (this.proxyConfiguration == null || this.proxyConfiguration.getMotdDefaultConfig().isEmpty()) {
          try {
            Thread.sleep(500);
          } catch (InterruptedException ignored) {
          }

          continue;
        }

        int count = this.atomicIntegers[0].getAndIncrement();
        this.currentMessageOfTheDayConfiguration = this.proxyConfiguration.getMotdDefaultConfig().get(count);

        if (count >= this.proxyConfiguration.getMotdDefaultConfig().size() - 1) {
          this.atomicIntegers[0].set(0);
        }

        try {
          Thread.sleep(TimeUnit.SECONDS.toMillis(this.currentMessageOfTheDayConfiguration.getWaitUntilNextInSeconds()));
        } catch (InterruptedException ignored) {
        }
      }
    });

    Constants.CACHED_THREAD_POOL.execute(() -> {
      while (!Thread.interrupted()) {
        if (this.proxyConfiguration == null || this.proxyConfiguration.getMotdMaintenanceConfig().isEmpty()) {
          try {
            Thread.sleep(500);
          } catch (InterruptedException ignored) {
          }

          continue;
        }

        int count = this.atomicIntegers[1].getAndIncrement();
        this.currentMaintenanceMessageOfTheDayConfiguration = this.proxyConfiguration.getMotdMaintenanceConfig().get(count);

        if (count >= this.proxyConfiguration.getMotdMaintenanceConfig().size() - 1) {
          this.atomicIntegers[1].set(0);
        }

        try {
          Thread.sleep(TimeUnit.SECONDS.toMillis(this.currentMaintenanceMessageOfTheDayConfiguration.getWaitUntilNextInSeconds()));
        } catch (InterruptedException ignored) {
        }
      }
    });

    Constants.CACHED_THREAD_POOL.execute(() -> {
      while (!Thread.interrupted()) {
        if (this.proxyConfiguration == null || this.proxyConfiguration.getTabListConfigurations().isEmpty()) {
          try {
            Thread.sleep(500);
          } catch (InterruptedException ignored) {
          }

          continue;
        }

        int count = this.atomicIntegers[2].getAndIncrement();
        this.currentTabListConfiguration = this.proxyConfiguration.getTabListConfigurations().get(count);
        this.handleTabListChange();

        if (count >= this.proxyConfiguration.getTabListConfigurations().size() - 1) {
          this.atomicIntegers[2].set(0);
        }

        try {
          Thread.sleep(TimeUnit.SECONDS.toMillis(this.currentTabListConfiguration.getWaitUntilNextInSeconds()));
        } catch (InterruptedException ignored) {
        }
      }
    });
  }
}
