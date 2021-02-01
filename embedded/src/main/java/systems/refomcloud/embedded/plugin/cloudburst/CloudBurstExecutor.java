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
package systems.refomcloud.embedded.plugin.cloudburst;

import org.cloudburstmc.server.Server;
import org.cloudburstmc.server.player.Player;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.embedded.Embedded;
import systems.refomcloud.embedded.shared.SharedInvalidPlayerFixer;
import systems.reformcloud.ExecutorType;
import systems.reformcloud.process.ProcessInformation;
import systems.reformcloud.shared.process.DefaultPlayer;

public final class CloudBurstExecutor extends Embedded {

  private static CloudBurstExecutor instance;
  private final Object plugin;

  protected CloudBurstExecutor(Object plugin) {
    super.type = ExecutorType.API;

    instance = this;
    this.plugin = plugin;

    this.fixInvalidPlayers();
  }

  @NotNull
  public static CloudBurstExecutor getInstance() {
    return instance;
  }

  @NotNull
  public Object getPlugin() {
    return this.plugin;
  }

  @Override
  protected int getMaxPlayersOfEnvironment() {
    return Server.getInstance().getMaxPlayers();
  }

  @Override
  protected void updatePlayersOfEnvironment(@NotNull ProcessInformation information) {
    for (Player player : Server.getInstance().getOnlinePlayers().values()) {
      if (!information.getPlayerByUniqueId(player.getServerId()).isPresent()) {
        information.getPlayers().add(new DefaultPlayer(player.getServerId(), player.getName(), System.currentTimeMillis()));
      }
    }
  }

  private void fixInvalidPlayers() {
    SharedInvalidPlayerFixer.start(
      uuid -> Server.getInstance().getPlayer(uuid).isPresent(),
      () -> Server.getInstance().getOnlinePlayers().size()
    );
  }
}
