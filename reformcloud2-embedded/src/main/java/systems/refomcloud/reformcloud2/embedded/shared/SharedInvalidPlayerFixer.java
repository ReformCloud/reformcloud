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
package systems.refomcloud.reformcloud2.embedded.shared;

import org.jetbrains.annotations.NotNull;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.reformcloud.reformcloud2.executor.api.groups.process.player.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.process.Player;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.shared.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public final class SharedInvalidPlayerFixer {

  private SharedInvalidPlayerFixer() {
    throw new UnsupportedOperationException();
  }

  public static void start(@NotNull Function<UUID, Boolean> onlineChecker, @NotNull Supplier<Integer> onlineCountSupplier) {
    Constants.SINGLE_THREAD_SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
      ProcessInformation current = Embedded.getInstance().getCurrentProcessInformation();
      Collection<Player> forRemoval = new ArrayList<>();

      for (Player onlinePlayer : current.getPlayers()) {
        if (onlinePlayer.getJoined() + TimeUnit.SECONDS.toMillis(5) > System.currentTimeMillis() || onlineChecker.apply(onlinePlayer.getUniqueID())) {
          continue;
        }
        forRemoval.add(onlinePlayer);
      }

      if (forRemoval.isEmpty()) {
        return;
      }

      current.getPlayers().removeAll(forRemoval);

      final PlayerAccessConfiguration configuration = current.getProcessGroup().getPlayerAccessConfiguration();
      if (configuration.isUsePlayerLimit() && onlineCountSupplier.get() < configuration.getMaxPlayers()
        && !current.getCurrentState().equals(ProcessState.READY)
        && !current.getCurrentState().equals(ProcessState.INVISIBLE)) {
        current.setCurrentState(ProcessState.READY);
      }

      Embedded.getInstance().updateCurrentProcessInformation();
    }, 2, 2, TimeUnit.SECONDS);
  }
}
