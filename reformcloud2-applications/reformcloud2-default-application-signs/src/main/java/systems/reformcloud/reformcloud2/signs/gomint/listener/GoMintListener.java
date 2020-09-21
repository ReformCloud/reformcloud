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
package systems.reformcloud.reformcloud2.signs.gomint.listener;

import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;
import io.gomint.event.player.PlayerInteractEvent;
import io.gomint.world.block.BlockSign;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.event.EventManager;
import systems.reformcloud.reformcloud2.signs.event.UserSignPreConnectEvent;
import systems.reformcloud.reformcloud2.signs.gomint.adapter.GoMintSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

public class GoMintListener implements EventListener {

    private final GoMintSignSystemAdapter goMintSignSystemAdapter;

    public GoMintListener(GoMintSignSystemAdapter goMintSignSystemAdapter) {
        this.goMintSignSystemAdapter = goMintSignSystemAdapter;
    }

    @EventHandler
    public void handle(@NotNull PlayerInteractEvent event) {
        if (event.getClickType() != PlayerInteractEvent.ClickType.LEFT || !(event.getBlock() instanceof BlockSign)) {
            return;
        }

        CloudSign cloudSign = this.goMintSignSystemAdapter.getSignAt(this.goMintSignSystemAdapter.getSignConverter().to((BlockSign) event.getBlock()));
        if (cloudSign == null) {
            return;
        }

        boolean canConnect = SignSystemAdapter.getInstance().canConnect(cloudSign, event.getPlayer().getPermissionManager()::hasPermission);
        if (!ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new UserSignPreConnectEvent(
            event.getPlayer().getUUID(), event.getPlayer().getPermissionManager()::hasPermission, cloudSign, canConnect
        )).isAllowConnection()) {
            return;
        }

        ExecutorAPI.getInstance().getPlayerProvider().getPlayer(event.getPlayer().getUUID())
            .ifPresent(wrapper -> wrapper.connect(cloudSign.getCurrentTarget().getProcessDetail().getName()));
    }
}
