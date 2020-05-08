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
package systems.reformcloud.reformcloud2.signs.nukkit.adapter;

import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.Sign;
import cn.nukkit.level.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.signs.util.converter.SignConverter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudLocation;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

import java.math.BigDecimal;

public class NukkitSignConverter implements SignConverter<Sign> {

    static final NukkitSignConverter INSTANCE = new NukkitSignConverter();

    @Nullable
    @Override
    public Sign from(@NotNull CloudSign cloudSign) {
        Location location = this.accumulate(cloudSign.getLocation());
        if (location == null) {
            return null;
        }

        BlockEntity entity = location.getLevel().getBlockEntity(location.getPosition().toInt());
        return entity instanceof Sign ? (Sign) entity : null;
    }

    @NotNull
    @Override
    public CloudSign to(@NotNull Sign sign, @NotNull String group) {
        return new CloudSign(group, accumulate(Location.from(sign.getPosition(), sign.getLevel())));
    }

    @NotNull
    @Override
    public CloudLocation to(@NotNull Sign sign) {
        return accumulate(Location.from(sign.getPosition(), sign.getLevel()));
    }

    @Nullable
    private Location accumulate(@NotNull CloudLocation cloudLocation) {
        if (Server.getInstance().getLevelByName(cloudLocation.getWorld()) == null) {
            return null;
        }

        return Location.from(
                (float) cloudLocation.getX(),
                (float) cloudLocation.getY(),
                (float) cloudLocation.getZ(),
                cloudLocation.getYaw(),
                cloudLocation.getPitch(),
                Server.getInstance().getLevelByName(cloudLocation.getWorld())
        );
    }

    @NotNull
    private CloudLocation accumulate(@NotNull Location location) {
        Conditions.isTrue(location.getLevel() != null);
        return new CloudLocation(
                location.getLevel().getName(),
                API.getInstance().getCurrentProcessInformation().getProcessGroup().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                BigDecimal.valueOf(location.getYaw()).floatValue(),
                BigDecimal.valueOf(location.getPitch()).floatValue()
        );
    }
}
