/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.reformcloud2.signs.sponge.adapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.reformcloud.reformcloud2.signs.util.converter.SignConverter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudLocation;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

public class SpongeSignConverter implements SignConverter<Sign> {

    static final SpongeSignConverter INSTANCE = new SpongeSignConverter();

    @Nullable
    @Override
    public Sign from(@NotNull CloudSign cloudSign) {
        Location<World> sponge = this.accumulate(cloudSign.getLocation());
        if (sponge == null) {
            return null;
        }

        if (sponge.getBlock().getType().equals(BlockTypes.STANDING_SIGN)
            || sponge.getBlock().getType().equals(BlockTypes.WALL_SIGN)) {
            TileEntity entity = sponge.getTileEntity().orElse(null);
            return entity instanceof Sign ? (Sign) entity : null;
        }

        return null;
    }

    @NotNull
    @Override
    public CloudSign to(@NotNull Sign sign, @NotNull String group) {
        return new CloudSign(group, this.accumulate(sign.getLocation()));
    }

    @NotNull
    @Override
    public CloudLocation to(@NotNull Sign sign) {
        return this.accumulate(sign.getLocation());
    }

    private Location<World> accumulate(CloudLocation location) {
        World world = Sponge.getServer().getWorld(location.getWorld()).orElse(null);
        if (world == null) {
            return null;
        }

        return new Location<>(world, location.getX(), location.getY(), location.getZ());
    }

    private CloudLocation accumulate(Location<World> location) {
        return new CloudLocation(
            location.getExtent().getName(),
            Embedded.getInstance().getCurrentProcessInformation().getProcessGroup().getName(),
            location.getX(),
            location.getY(),
            location.getZ(),
            -1,
            -1
        );
    }
}
