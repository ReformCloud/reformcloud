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
package systems.refomcloud.embedded.plugin.waterdog.reconnect;

import dev.waterdog.ProxyServer;
import dev.waterdog.network.ServerInfo;
import dev.waterdog.player.ProxiedPlayer;
import dev.waterdog.utils.types.IJoinHandler;
import dev.waterdog.utils.types.IReconnectHandler;
import systems.refomcloud.embedded.controller.ProxyServerController;
import systems.refomcloud.embedded.plugin.waterdog.fallback.WaterDogFallbackExtraFilter;
import systems.refomcloud.embedded.shared.SharedPlayerFallbackFilter;
import systems.reformcloud.ExecutorAPI;

public final class ReformCloudConnectionHandler implements IReconnectHandler, IJoinHandler {

  public static ReformCloudConnectionHandler INSTANCE = new ReformCloudConnectionHandler();

  private ReformCloudConnectionHandler() {
  }

  @Override
  public ServerInfo getFallbackServer(ProxiedPlayer proxiedPlayer, ServerInfo serverInfo, String s) {
    return this.determineServer(proxiedPlayer);
  }

  @Override
  public ServerInfo determineServer(ProxiedPlayer player) {
    return SharedPlayerFallbackFilter.filterFallback(
      player.getUniqueId(),
      ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ProxyServerController.class).getCachedLobbyServers(),
      player::hasPermission,
      WaterDogFallbackExtraFilter.INSTANCE,
      player.getServerInfo() == null ? null : player.getServerInfo().getServerName()
    ).map(info -> ProxyServer.getInstance().getServerInfo(info.getName())).orElse(null);
  }
}
