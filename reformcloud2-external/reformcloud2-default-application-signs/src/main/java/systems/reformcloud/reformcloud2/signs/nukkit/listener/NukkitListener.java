package systems.reformcloud.reformcloud2.signs.nukkit.listener;

import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.signs.nukkit.adapter.NukkitSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

public class NukkitListener implements Listener {

    @EventHandler
    public void handle(final PlayerInteractEvent event) {
        if (event.getAction().equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) {
            if (event.getBlock().getId() == 68 || event.getBlock().getId() == 63) {
                if (!(event.getBlock().getLevel().getBlockEntity(event.getBlock().getLocation()) instanceof BlockEntitySign)) {
                    return;
                }

                BlockEntitySign sign = (BlockEntitySign) event.getBlock().getLevel().getBlockEntity(event.getBlock().getLocation());
                CloudSign cloudSign = NukkitSignSystemAdapter.getInstance().getSignAt(
                        NukkitSignSystemAdapter.getInstance().getSignConverter().to(sign)
                );
                if (cloudSign == null || !SignSystemAdapter.getInstance().canConnect(cloudSign)) {
                    return;
                }

                ExecutorAPI.getInstance().connect(event.getPlayer().getUniqueId(), cloudSign.getCurrentTarget());
            }
        }
    }
}
