package systems.reformcloud.reformcloud2.signs.bukkit.adapter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.signs.util.converter.SignConverter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudLocation;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BukkitSignConverter implements SignConverter<Sign> {

    static final BukkitSignConverter INSTANCE = new BukkitSignConverter();

    @Nullable
    @Override
    public Sign from(@Nonnull CloudSign cloudSign) {
        Location bukkit = accumulate(cloudSign.getLocation());
        return bukkit != null && bukkit.getBlock().getState() instanceof Sign ? (Sign) bukkit.getBlock().getState() : null;
    }

    @Nonnull
    @Override
    public CloudSign to(@Nonnull Sign sign, @Nonnull String group) {
        return new CloudSign(group, accumulate(sign.getLocation().clone()));
    }

    @Nonnull
    @Override
    public CloudLocation to(@Nonnull Sign sign) {
        return accumulate(sign.getLocation().clone());
    }

    private Location accumulate(CloudLocation location) {
        if (Bukkit.getWorld(location.getWorld()) == null) {
            return null;
        }

        return new Location(
                Bukkit.getWorld(location.getWorld()),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }

    private CloudLocation accumulate(Location location) {
        Conditions.isTrue(location.getWorld() != null);
        Conditions.isTrue(ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getThisProcessInformation() != null);

        return new CloudLocation(
                location.getWorld().getName(),
                ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getThisProcessInformation().getProcessGroup().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }
}
