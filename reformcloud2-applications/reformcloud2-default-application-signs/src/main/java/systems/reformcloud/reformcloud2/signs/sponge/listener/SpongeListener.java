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
package systems.reformcloud.reformcloud2.signs.sponge.listener;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.event.EventManager;
import systems.reformcloud.reformcloud2.signs.event.UserSignPreConnectEvent;
import systems.reformcloud.reformcloud2.signs.sponge.adapter.SpongeSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

public class SpongeListener {

    @Listener
    public void handle(final InteractBlockEvent event, @First Player player) {
        BlockState blockState = event.getTargetBlock().getState();
        if (blockState.getType().equals(BlockTypes.STANDING_SIGN) || blockState.getType().equals(BlockTypes.WALL_SIGN)) {
            if (event.getTargetBlock().getLocation().isPresent()
                && event.getTargetBlock().getLocation().get().getTileEntity().isPresent()
                && event.getTargetBlock().getLocation().get().getTileEntity().orElse(null) instanceof Sign
            ) {
                Sign sign = (Sign) event.getTargetBlock().getLocation().get().getTileEntity().get();
                CloudSign cloudSign = SpongeSignSystemAdapter.getInstance().getSignAt(
                    SpongeSignSystemAdapter.getInstance().getSignConverter().to(sign)
                );
                if (cloudSign == null) {
                    return;
                }

                boolean canConnect = SignSystemAdapter.getInstance().canConnect(cloudSign, player::hasPermission);
                if (!ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new UserSignPreConnectEvent(
                    player.getUniqueId(), player::hasPermission, cloudSign, canConnect
                )).isAllowConnection()) {
                    return;
                }

                ExecutorAPI.getInstance().getPlayerProvider().getPlayer(player.getUniqueId())
                    .ifPresent(wrapper -> wrapper.connect(cloudSign.getCurrentTarget().getProcessDetail().getName()));
            }
        }
    }
}
