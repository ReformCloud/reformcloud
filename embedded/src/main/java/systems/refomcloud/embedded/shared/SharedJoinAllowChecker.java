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
import systems.refomcloud.embedded.Embedded;
import systems.reformcloud.group.messages.IngameMessages;
import systems.reformcloud.group.process.player.PlayerAccessConfiguration;
import systems.reformcloud.process.ProcessInformation;
import systems.reformcloud.process.ProcessState;
import systems.reformcloud.shared.collect.Entry2;
import systems.reformcloud.shared.process.DefaultPlayer;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;

public final class SharedJoinAllowChecker {

  private SharedJoinAllowChecker() {
    throw new UnsupportedOperationException();
  }

  @NotNull
  public static Entry2<Boolean, String> checkIfConnectAllowed(@NotNull Function<String, Boolean> permissionChecker,
                                                              @NotNull IngameMessages messages,
                                                              @Nullable Collection<ProcessInformation> checkedConnectedServices,
                                                              @NotNull UUID playerUniqueId,
                                                              @NotNull String username) {
    ProcessInformation current = Embedded.getInstance().getCurrentProcessInformation();
    PlayerAccessConfiguration configuration = current.getProcessGroup().getPlayerAccessConfiguration();

    if (checkedConnectedServices != null
      && checkedConnectedServices.stream().anyMatch(e -> e.getPlayerByUniqueId(playerUniqueId).isPresent())) {
      return new Entry2<>(false, messages.format(messages.getAlreadyConnectedToNetwork()));
    }

    if (configuration.isUsePlayerLimit() && configuration.getMaxPlayers() <= current.getOnlineCount()
      && !permissionChecker.apply(configuration.getFullJoinPermission())) {
      return new Entry2<>(false, messages.format(messages.getProcessFullMessage()));
    }

    if (configuration.isJoinOnlyWithPermission() && !permissionChecker.apply(configuration.getJoinPermission())) {
      return new Entry2<>(false, messages.format(messages.getProcessEnterPermissionNotSet()));
    }

    if (configuration.isMaintenance() && !permissionChecker.apply(configuration.getMaintenanceJoinPermission())) {
      return new Entry2<>(false, messages.format(messages.getProcessInMaintenanceMessage()));
    }

    if (configuration.isUsePlayerLimit()
      && current.getCurrentState().equals(ProcessState.READY)
      && configuration.getMaxPlayers() <= current.getOnlineCount() + 1) {
      current.setCurrentState(ProcessState.FULL);
    }

    current.getPlayers().add(new DefaultPlayer(playerUniqueId, username, System.currentTimeMillis()));
    Embedded.getInstance().updateCurrentProcessInformation();

    return new Entry2<>(true, null);
  }
}
