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
package systems.refomcloud.embedded.shared;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.refomcloud.embedded.event.PlayerFallbackChooseEvent;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.event.EventManager;
import systems.reformcloud.process.ProcessInformation;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

public final class SharedPlayerFallbackFilter {

  private SharedPlayerFallbackFilter() {
    throw new UnsupportedOperationException();
  }

  @NotNull
  public static Optional<ProcessInformation> filterFallback(@NotNull UUID playerUniqueId,
                                                            @NotNull Collection<ProcessInformation> lobbies,
                                                            @NotNull Function<String, Boolean> permissionChecker,
                                                            @NotNull Predicate<ProcessInformation> extraFilter,
                                                            @Nullable String currentServer) {
    if (lobbies.isEmpty()) {
      return Optional.empty();
    }

    ProcessInformation filtered = lobbies
      .stream()
      .filter(lobby -> lobby.getPrimaryTemplate().getVersion().getVersionType().isServer())
      .filter(lobby -> lobby.getCurrentState().isOnline())
      .filter(lobby -> !lobby.getName().equals(currentServer))
      .filter(extraFilter)
      .filter(lobby -> {
        boolean maintenance = lobby.getProcessGroup().getPlayerAccessConfiguration().isMaintenance();
        return !maintenance || permissionChecker.apply(lobby.getProcessGroup().getPlayerAccessConfiguration().getMaintenanceJoinPermission());
      })
      .filter(lobby -> {
        boolean requiredJoinPermission = lobby.getProcessGroup().getPlayerAccessConfiguration().isJoinOnlyWithPermission();
        return !requiredJoinPermission || permissionChecker.apply(lobby.getProcessGroup().getPlayerAccessConfiguration().getJoinPermission());
      })
      .filter(lobby -> {
        boolean cloudPlayerLimit = lobby.getProcessGroup().getPlayerAccessConfiguration().isUsePlayerLimit();
        boolean full = lobby.getOnlineCount() >= lobby.getProcessGroup().getPlayerAccessConfiguration().getMaxPlayers();
        return !cloudPlayerLimit || !full || permissionChecker.apply(lobby.getProcessGroup().getPlayerAccessConfiguration().getFullJoinPermission());
      })
      .min(ProcessPriorityComparable.INSTANCE::compare)
      .orElse(null);

    return ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new PlayerFallbackChooseEvent(
      playerUniqueId, filtered, lobbies
    )).getFilteredFallback();
  }

  private static class ProcessPriorityComparable implements Comparator<ProcessInformation> {

    private static final Comparator<ProcessInformation> INSTANCE = new ProcessPriorityComparable();

    @Override
    public int compare(ProcessInformation o1, ProcessInformation o2) {
      // reversed
      int result = Boolean.compare(
        o2.getProcessGroup().getPlayerAccessConfiguration().isJoinOnlyWithPermission(),
        o1.getProcessGroup().getPlayerAccessConfiguration().isJoinOnlyWithPermission()
      );
      if (result != 0) {
        // if the result is not the same compared to the other we'll go on and do the next check
        // else we return the value which we have found out before
        // see: https://en.wikipedia.org/wiki/Three-way_comparison
        return result;
      }
      return Integer.compare(o1.getOnlineCount(), o2.getOnlineCount());
    }
  }
}
