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
package systems.reformcloud.reformcloud2.signs.bukkit.adapter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.base.Conditions;
import systems.reformcloud.reformcloud2.signs.util.converter.SignConverter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudLocation;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

public class BukkitSignConverter implements SignConverter<Sign> {

    static final BukkitSignConverter INSTANCE = new BukkitSignConverter();

    @Nullable
    @Override
    public Sign from(@NotNull CloudSign cloudSign) {
        Conditions.isTrue(Bukkit.isPrimaryThread(), "Cannot call method from async on spigot servers!");

        Location bukkit = this.accumulate(cloudSign.getLocation());
        return bukkit != null && bukkit.getBlock().getState() instanceof Sign ? (Sign) bukkit.getBlock().getState() : null;
    }

    @NotNull
    @Override
    public CloudSign to(@NotNull Sign sign, @NotNull String group) {
        return new CloudSign(group, this.accumulate(sign.getLocation().clone()));
    }

    @NotNull
    @Override
    public CloudLocation to(@NotNull Sign sign) {
        return this.accumulate(sign.getLocation().clone());
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
