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
package systems.reformcloud.reformcloud2.signs.bukkit.listener;

import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.event.EventManager;
import systems.reformcloud.reformcloud2.signs.bukkit.adapter.BukkitSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.event.UserSignPreConnectEvent;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

public class BukkitListener implements Listener {

    @EventHandler
    public void handle(final PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
            && event.getClickedBlock() != null
            && event.getClickedBlock().getState() instanceof Sign
        ) {
            Sign sign = (Sign) event.getClickedBlock().getState();
            CloudSign cloudSign = BukkitSignSystemAdapter.getInstance().getSignAt(
                BukkitSignSystemAdapter.getInstance().getSignConverter().to(sign)
            );

            if (cloudSign == null) {
                return;
            }

            boolean canConnect = SignSystemAdapter.getInstance().canConnect(cloudSign, event.getPlayer()::hasPermission);
            if (!ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new UserSignPreConnectEvent(
                event.getPlayer().getUniqueId(), event.getPlayer()::hasPermission, cloudSign, canConnect
            )).isAllowConnection()) {
                return;
            }

            ExecutorAPI.getInstance().getPlayerProvider().getPlayer(event.getPlayer().getUniqueId())
                .ifPresent(wrapper -> wrapper.connect(cloudSign.getCurrentTarget().getProcessDetail().getName()));
        }
    }
}
