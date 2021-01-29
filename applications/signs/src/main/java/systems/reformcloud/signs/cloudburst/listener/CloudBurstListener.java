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
package systems.reformcloud.signs.cloudburst.listener;

import org.cloudburstmc.server.blockentity.Sign;
import org.cloudburstmc.server.event.Listener;
import org.cloudburstmc.server.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.event.EventManager;
import systems.reformcloud.signs.cloudburst.adapter.CloudBurstSignSystemAdapter;
import systems.reformcloud.signs.event.UserSignPreConnectEvent;
import systems.reformcloud.signs.util.SignSystemAdapter;
import systems.reformcloud.signs.util.sign.CloudSign;

public class CloudBurstListener {

  @Listener
  public void handle(final @NotNull PlayerInteractEvent event) {
    CloudBurstSignSystemAdapter signSystemAdapter = CloudBurstSignSystemAdapter.getInstance();

    if (event.getAction().equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
      if (event.getBlock().getState().getType().getName().endsWith("_sign")) {
        if (!(event.getBlock().getLevel().getBlockEntity(event.getBlock().getPosition()) instanceof Sign)) {
          return;
        }

        Sign sign = (Sign) event.getBlock().getLevel().getBlockEntity(event.getBlock().getPosition());
        CloudSign cloudSign = signSystemAdapter.getSignAt(signSystemAdapter.getSignConverter().to(sign));
        if (cloudSign == null) {
          return;
        }

        boolean canConnect = SignSystemAdapter.getInstance().canConnect(cloudSign, event.getPlayer()::hasPermission);
        if (!ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new UserSignPreConnectEvent(
          event.getPlayer().getServerId(), event.getPlayer()::hasPermission, cloudSign, canConnect
        )).isAllowConnection()) {
          return;
        }

        ExecutorAPI.getInstance().getPlayerProvider().getPlayer(event.getPlayer().getServerId())
          .ifPresent(wrapper -> wrapper.connect(cloudSign.getCurrentTarget().getName()));
      }
    }
  }
}
