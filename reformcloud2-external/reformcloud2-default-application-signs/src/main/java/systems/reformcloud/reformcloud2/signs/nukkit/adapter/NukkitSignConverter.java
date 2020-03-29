package systems.reformcloud.reformcloud2.signs.nukkit.adapter;

import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.level.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.signs.util.converter.SignConverter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudLocation;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

import java.math.BigDecimal;

public class NukkitSignConverter implements SignConverter<BlockEntitySign> {

    static final NukkitSignConverter INSTANCE = new NukkitSignConverter();

    @Nullable
    @Override
    public BlockEntitySign from(@NotNull CloudSign cloudSign) {
        Location location = accumulate(cloudSign.getLocation());
        return location != null && location.getLevel().getBlockEntity(location) instanceof BlockEntitySign
                ? (BlockEntitySign) location.getLevel().getBlockEntity(location)
                : null;
    }

    @NotNull
    @Override
    public CloudSign to(@NotNull BlockEntitySign blockEntitySign, @NotNull String group) {
        return new CloudSign(group, accumulate(blockEntitySign.getLocation().clone()));
    }

    @NotNull
    @Override
    public CloudLocation to(@NotNull BlockEntitySign blockEntitySign) {
        return accumulate(blockEntitySign.getLocation().clone());
    }

    private Location accumulate(CloudLocation cloudLocation) {
        if (Server.getInstance().getLevelByName(cloudLocation.getWorld()) == null) {
            return null;
        }

        return new Location(
                cloudLocation.getX(),
                cloudLocation.getY(),
                cloudLocation.getZ(),
                cloudLocation.getYaw(),
                cloudLocation.getPitch(),
                Server.getInstance().getLevelByName(cloudLocation.getWorld())
        );
    }

    private CloudLocation accumulate(Location location) {
        Conditions.isTrue(location.getLevel() != null);
        return new CloudLocation(
                location.getLevel().getName(),
                API.getInstance().getCurrentProcessInformation().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                BigDecimal.valueOf(location.getYaw()).floatValue(),
                BigDecimal.valueOf(location.getPitch()).floatValue()
        );
    }
}
