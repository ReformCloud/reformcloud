/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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
package systems.reformcloud.reformcloud2.signs.nukkit.listener;

import cn.nukkit.blockentity.Sign;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.signs.nukkit.adapter.NukkitSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

public class NukkitListener implements Listener {

    @EventHandler
    public void handle(final @NotNull PlayerInteractEvent event) {
        NukkitSignSystemAdapter signSystemAdapter = NukkitSignSystemAdapter.getInstance();

        if (event.getAction().equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
            if (event.getBlock().getLevel().getBlockEntity(event.getBlock().getPosition()) instanceof Sign) {
                Sign sign = (Sign) event.getBlock().getLevel().getBlockEntity(event.getBlock().getPosition());
                CloudSign cloudSign = signSystemAdapter.getSignAt(signSystemAdapter.getSignConverter().to(sign));
                if (cloudSign == null || !SignSystemAdapter.getInstance().canConnect(cloudSign)) {
                    return;
                }

                ExecutorAPI.getInstance().getSyncAPI().getPlayerSyncAPI().connect(
                        event.getPlayer().getServerId(),
                        cloudSign.getCurrentTarget()
                );
            }
        }
    }
}
