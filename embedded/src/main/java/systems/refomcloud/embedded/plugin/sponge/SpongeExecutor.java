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
package systems.refomcloud.embedded.plugin.sponge;

import net.kyori.adventure.platform.spongeapi.SpongeAudiences;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import systems.refomcloud.embedded.Embedded;
import systems.refomcloud.embedded.executor.PlayerAPIExecutor;
import systems.refomcloud.embedded.plugin.sponge.executor.SpongePlayerExecutor;
import systems.refomcloud.embedded.shared.SharedInvalidPlayerFixer;
import systems.reformcloud.ExecutorType;
import systems.reformcloud.process.ProcessInformation;
import systems.reformcloud.shared.process.DefaultPlayer;

public final class SpongeExecutor extends Embedded {

  private static SpongeExecutor instance;
  private final SpongeLauncher plugin;
  private final SpongeExecutorService executorService;

  SpongeExecutor(SpongeLauncher launcher, SpongeAudiences audiences) {
    super.type = ExecutorType.API;
    PlayerAPIExecutor.setInstance(new SpongePlayerExecutor(audiences));

    this.plugin = launcher;
    this.executorService = Sponge.getScheduler().createSyncExecutor(launcher);
    instance = this;

    this.fixInvalidPlayers();
  }

  @NotNull
  public static SpongeExecutor getInstance() {
    return instance;
  }

  @Override
  public int getPlayerCount() {
    return Sponge.getServer().getOnlinePlayers().size();
  }

  @Override
  protected int getMaxPlayersOfEnvironment() {
    return Sponge.getServer().getMaxPlayers();
  }

  @Override
  protected void updatePlayersOfEnvironment(@NotNull ProcessInformation information) {
    for (Player player : Sponge.getServer().getOnlinePlayers()) {
      if (!information.getPlayerByUniqueId(player.getUniqueId()).isPresent()) {
        information.getPlayers().add(new DefaultPlayer(player.getUniqueId(), player.getName(), System.currentTimeMillis()));
      }
    }
  }

  @NotNull
  public SpongeLauncher getPlugin() {
    return this.plugin;
  }

  @NotNull
  public SpongeExecutorService getExecutorService() {
    return this.executorService;
  }

  private void fixInvalidPlayers() {
    SharedInvalidPlayerFixer.start(
      uuid -> Sponge.getServer().getPlayer(uuid).isPresent(),
      () -> Sponge.getServer().getOnlinePlayers().size()
    );
  }
}
