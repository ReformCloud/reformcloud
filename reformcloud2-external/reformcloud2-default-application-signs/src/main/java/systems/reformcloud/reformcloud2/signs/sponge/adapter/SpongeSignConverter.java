package systems.reformcloud.reformcloud2.signs.sponge.adapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.signs.util.converter.SignConverter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudLocation;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

public class SpongeSignConverter implements SignConverter<Sign> {

  static final SpongeSignConverter INSTANCE = new SpongeSignConverter();

  @Nullable
  @Override
  public Sign from(@Nonnull CloudSign cloudSign) {
    Location<World> sponge = accumulate(cloudSign.getLocation());
    if (sponge == null) {
      return null;
    }

    if (sponge.getBlock().getType().equals(BlockTypes.STANDING_SIGN) ||
        sponge.getBlock().getType().equals(BlockTypes.WALL_SIGN)) {
      TileEntity entity = sponge.getTileEntity().orElse(null);
      return entity instanceof Sign ? (Sign)entity : null;
    }

    return null;
  }

  @Nonnull
  @Override
  public CloudSign to(@Nonnull Sign sign, @Nonnull String group) {
    return new CloudSign(group, accumulate(sign.getLocation()));
  }

  @Nonnull
  @Override
  public CloudLocation to(@Nonnull Sign sign) {
    return accumulate(sign.getLocation());
  }

  private Location<World> accumulate(CloudLocation location) {
    World world = Sponge.getServer().getWorld(location.getWorld()).orElse(null);
    if (world == null) {
      return null;
    }

    return new Location<>(world, location.getX(), location.getY(),
                          location.getZ());
  }

  private CloudLocation accumulate(Location<World> location) {
    Conditions.isTrue(ExecutorAPI.getInstance()
                          .getSyncAPI()
                          .getProcessSyncAPI()
                          .getThisProcessInformation() != null);

    return new CloudLocation(location.getExtent().getName(),
                             ExecutorAPI.getInstance()
                                 .getSyncAPI()
                                 .getProcessSyncAPI()
                                 .getThisProcessInformation()
                                 .getProcessGroup()
                                 .getName(),
                             location.getX(), location.getY(), location.getZ(),
                             -1, -1);
  }
}
