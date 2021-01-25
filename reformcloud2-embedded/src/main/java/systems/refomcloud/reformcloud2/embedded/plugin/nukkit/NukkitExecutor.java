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
package systems.refomcloud.reformcloud2.embedded.plugin.nukkit;

import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.refomcloud.reformcloud2.embedded.executor.PlayerAPIExecutor;
import systems.refomcloud.reformcloud2.embedded.plugin.nukkit.executor.NukkitPlayerAPIExecutor;
import systems.refomcloud.reformcloud2.embedded.shared.SharedInvalidPlayerFixer;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;

public final class NukkitExecutor extends Embedded {

  private static NukkitExecutor instance;
  private final Plugin plugin;

  NukkitExecutor(Plugin plugin) {
    super.type = ExecutorType.API;
    PlayerAPIExecutor.setInstance(new NukkitPlayerAPIExecutor());

    instance = this;
    this.plugin = plugin;

    this.fixInvalidPlayers();
  }

  @NotNull
  public static NukkitExecutor getInstance() {
    return instance;
  }

  @Override
  protected int getMaxPlayersOfEnvironment() {
    return Server.getInstance().getMaxPlayers();
  }

  @NotNull
  public Plugin getPlugin() {
    return this.plugin;
  }

  private void fixInvalidPlayers() {
    SharedInvalidPlayerFixer.start(
      uuid -> Server.getInstance().getPlayer(uuid).isPresent(),
      () -> Server.getInstance().getOnlinePlayers().size()
    );
  }
}
