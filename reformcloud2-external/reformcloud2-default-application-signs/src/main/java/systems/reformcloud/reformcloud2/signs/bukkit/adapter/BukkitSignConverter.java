package systems.reformcloud.reformcloud2.signs.bukkit.adapter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.signs.util.converter.SignConverter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudLocation;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

public class BukkitSignConverter implements SignConverter<Sign> {

    static final BukkitSignConverter INSTANCE = new BukkitSignConverter();

    @Nullable
    @Override
    public Sign from(@NotNull CloudSign cloudSign) {
        Location bukkit = accumulate(cloudSign.getLocation());
        return bukkit != null && bukkit.getBlock().getState() instanceof Sign ? (Sign) bukkit.getBlock().getState() : null;
    }

    @NotNull
    @Override
    public CloudSign to(@NotNull Sign sign, @NotNull String group) {
        return new CloudSign(group, accumulate(sign.getLocation().clone()));
    }

    @NotNull
    @Override
    public CloudLocation to(@NotNull Sign sign) {
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
        return new CloudLocation(
                location.getWorld().getName(),
                API.getInstance().getCurrentProcessInformation().getProcessGroup().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }
}
