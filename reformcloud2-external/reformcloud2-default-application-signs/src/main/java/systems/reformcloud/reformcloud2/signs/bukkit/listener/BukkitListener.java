package systems.reformcloud.reformcloud2.signs.bukkit.listener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import systems.reformcloud.reformcloud2.signs.bukkit.adapter.BukkitSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

public class BukkitListener implements Listener {

  @EventHandler
  public void handle(final PlayerInteractEvent event) {
    if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) &&
        event.getClickedBlock() != null &&
        event.getClickedBlock().getState() instanceof Sign) {
      Sign sign = (Sign)event.getClickedBlock().getState();
      CloudSign cloudSign = BukkitSignSystemAdapter.getInstance().getSignAt(
          BukkitSignSystemAdapter.getInstance().getSignConverter().to(sign));

      if (cloudSign == null ||
          !SignSystemAdapter.getInstance().canConnect(cloudSign)) {
        return;
      }

      connect(event.getPlayer(), cloudSign.getCurrentTarget().getName());
    }
  }

  private void connect(Player player, String target) {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         DataOutputStream dataOutputStream =
             new DataOutputStream(outputStream)) {
      dataOutputStream.writeUTF("Connect");
      dataOutputStream.writeUTF(target);
      player.sendPluginMessage(
          BukkitSignSystemAdapter.getInstance().getPlugin(), "BungeeCord",
          outputStream.toByteArray());
    } catch (final IOException ex) {
      ex.printStackTrace();
    }
  }
}
