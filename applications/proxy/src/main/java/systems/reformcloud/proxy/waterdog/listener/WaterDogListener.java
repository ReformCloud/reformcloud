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
package systems.reformcloud.proxy.waterdog.listener;

import dev.waterdog.event.defaults.ProxyPingEvent;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.embedded.Embedded;
import systems.reformcloud.proxy.ProxyConfigurationHandler;

import java.util.Collections;
import java.util.function.Consumer;

public final class WaterDogListener {

  private WaterDogListener() {
    throw new UnsupportedOperationException();
  }

  @NotNull
  public static Consumer<ProxyPingEvent> forProxyPing() {
    return event -> ProxyConfigurationHandler.getInstance().getBestMessageOfTheDayConfiguration().ifPresent(config -> {
      event.setPlayers(Collections.emptyList());
      event.setPlayerCount(Embedded.getInstance().getPlayerCount());
      event.setMaximumPlayerCount(Embedded.getInstance().getCurrentProcessInformation().getProcessGroup().getPlayerAccessConfiguration().getMaxPlayers());
      event.setMotd(ProxyConfigurationHandler.getInstance().replaceMessageOfTheDayPlaceHolders(config.getFirstLine() + '\n' + config.getSecondLine()));
    });
  }
}
