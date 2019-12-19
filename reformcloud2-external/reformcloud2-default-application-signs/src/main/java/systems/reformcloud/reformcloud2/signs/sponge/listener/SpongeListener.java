package systems.reformcloud.reformcloud2.signs.sponge.listener;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.signs.sponge.adapter.SpongeSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

public class SpongeListener {

  @Listener
  public void handle(final InteractBlockEvent event, @First Player player) {
    BlockState blockState = event.getTargetBlock().getState();
    if (blockState.getType().equals(BlockTypes.STANDING_SIGN) ||
        blockState.getType().equals(BlockTypes.WALL_SIGN)) {
      if (event.getTargetBlock().getLocation().isPresent() &&
          event.getTargetBlock()
              .getLocation()
              .get()
              .getTileEntity()
              .isPresent() &&
          event.getTargetBlock().getLocation().get().getTileEntity().orElse(
              null)
                  instanceof Sign) {
        Sign sign = (Sign)event.getTargetBlock()
                        .getLocation()
                        .get()
                        .getTileEntity()
                        .get();
        CloudSign cloudSign = SpongeSignSystemAdapter.getInstance().getSignAt(
            SpongeSignSystemAdapter.getInstance().getSignConverter().to(sign));
        if (cloudSign == null ||
            !SignSystemAdapter.getInstance().canConnect(cloudSign)) {
          return;
        }

        ExecutorAPI.getInstance().getSyncAPI().getPlayerSyncAPI().connect(
            player.getUniqueId(), cloudSign.getCurrentTarget());
      }
    }
  }
}
