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
package systems.refomcloud.embedded.plugin.waterdog;

import dev.waterdog.ProxyServer;
import dev.waterdog.event.EventPriority;
import dev.waterdog.event.defaults.PlayerDisconnectEvent;
import dev.waterdog.event.defaults.PlayerLoginEvent;
import dev.waterdog.player.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.embedded.Embedded;
import systems.refomcloud.embedded.controller.ProcessEventHandler;
import systems.refomcloud.embedded.controller.ProxyServerController;
import systems.refomcloud.embedded.executor.PlayerAPIExecutor;
import systems.refomcloud.embedded.plugin.waterdog.controller.WaterDogProxyServerController;
import systems.refomcloud.embedded.plugin.waterdog.event.PlayerListenerHandler;
import systems.refomcloud.embedded.plugin.waterdog.executor.WaterDogPlayerExecutor;
import systems.refomcloud.embedded.plugin.waterdog.reconnect.ReformCloudConnectionHandler;
import systems.refomcloud.embedded.shared.SharedInvalidPlayerFixer;
import systems.reformcloud.ExecutorType;
import systems.reformcloud.event.EventManager;
import systems.reformcloud.process.ProcessInformation;
import systems.reformcloud.shared.process.DefaultPlayer;

public final class WaterDogExecutor extends Embedded {

  public WaterDogExecutor() {
    super.type = ExecutorType.API;

    PlayerAPIExecutor.setInstance(new WaterDogPlayerExecutor());

    ProxyServer.getInstance().getEventManager().subscribe(PlayerLoginEvent.class, PlayerListenerHandler.handlerForLogin(), EventPriority.HIGH);
    ProxyServer.getInstance().getEventManager().subscribe(PlayerDisconnectEvent.class, PlayerListenerHandler.handlerForDisconnect(), EventPriority.LOWEST);

    ProxyServer.getInstance().setJoinHandler(ReformCloudConnectionHandler.INSTANCE);
    ProxyServer.getInstance().setReconnectHandler(ReformCloudConnectionHandler.INSTANCE);

    super.getServiceRegistry().setProvider(ProxyServerController.class, new WaterDogProxyServerController(), true);
    super.getServiceRegistry().getProviderUnchecked(EventManager.class).registerListener(new ProcessEventHandler());

    this.fixInvalidPlayers();
  }

  @Override
  public int getPlayerCount() {
    return ProxyServer.getInstance().getPlayers().size();
  }

  @Override
  protected int getMaxPlayersOfEnvironment() {
    return ProxyServer.getInstance().getConfiguration().getMaxPlayerCount();
  }

  @Override
  protected void updatePlayersOfEnvironment(@NotNull ProcessInformation information) {
    for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers().values()) {
      if (!information.getPlayerByUniqueId(player.getUniqueId()).isPresent()) {
        information.getPlayers().add(new DefaultPlayer(player.getUniqueId(), player.getName(), System.currentTimeMillis()));
      }
    }
  }

  private void fixInvalidPlayers() {
    SharedInvalidPlayerFixer.start(
      uuid -> ProxyServer.getInstance().getPlayer(uuid) != null,
      () -> ProxyServer.getInstance().getPlayers().size()
    );
  }
}
