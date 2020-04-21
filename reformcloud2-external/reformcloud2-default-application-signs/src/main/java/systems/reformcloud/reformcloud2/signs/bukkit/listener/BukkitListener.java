package systems.reformcloud.reformcloud2.signs.bukkit.listener;

import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.signs.bukkit.adapter.BukkitSignSystemAdapter;
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

            if (cloudSign == null || !SignSystemAdapter.getInstance().canConnect(cloudSign)) {
                return;
            }

            ExecutorAPI.getInstance().getSyncAPI().getPlayerSyncAPI().connect(
                    event.getPlayer().getUniqueId(),
                    cloudSign.getCurrentTarget().getProcessDetail().getName()
            );
        }
    }
}
