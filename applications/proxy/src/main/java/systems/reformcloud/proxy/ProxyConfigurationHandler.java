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
package systems.reformcloud.proxy;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.event.EventManager;
import systems.reformcloud.proxy.config.MotdConfiguration;
import systems.reformcloud.proxy.config.TabListConfiguration;
import systems.reformcloud.proxy.event.ProxyConfigurationHandlerSetupEvent;

import java.util.Objects;
import java.util.Optional;

public abstract class ProxyConfigurationHandler {

  private static ProxyConfigurationHandler instance;

  @NotNull
  public static ProxyConfigurationHandler getInstance() {
    return Objects.requireNonNull(instance, "The proxy config handler is configured yet");
  }

  @ApiStatus.Internal
  public static void setup() {
    ProxyConfigurationHandlerSetupEvent setupEvent = new ProxyConfigurationHandlerSetupEvent();
    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(setupEvent);
    ProxyConfigurationHandler.instance = setupEvent.getProxyConfigurationHandler().enable();
  }

  @NotNull
  public static String translateAlternateColorCodes(char altColorChar, @NotNull String textToTranslate) {
    char[] b = textToTranslate.toCharArray();

    for (int i = 0; i < b.length - 1; ++i) {
      if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
        b[i] = 167;
        b[i + 1] = Character.toLowerCase(b[i + 1]);
      }
    }

    return new String(b);
  }

  @NotNull
  @ApiStatus.Internal
  public abstract ProxyConfigurationHandler enable();

  @NotNull
  public abstract Optional<ProxyConfiguration> getProxyConfiguration();

  @NotNull
  public abstract Optional<TabListConfiguration> getCurrentTabListConfiguration();

  @NotNull
  public abstract Optional<MotdConfiguration> getCurrentMessageOfTheDayConfiguration();

  @NotNull
  public abstract Optional<MotdConfiguration> getCurrentMaintenanceMessageOfTheDayConfiguration();

  @NotNull
  public abstract Optional<MotdConfiguration> getBestMessageOfTheDayConfiguration();

  public abstract void handleTabListChange();

  @NotNull
  public abstract String replaceMessageOfTheDayPlaceHolders(@NotNull String messageOfTheDay);

  @NotNull
  public abstract String replaceTabListPlaceHolders(@NotNull String tabList);

  @ApiStatus.Internal
  public abstract void handleProxyConfigUpdate(@NotNull ProxyConfiguration proxyConfiguration);
}
