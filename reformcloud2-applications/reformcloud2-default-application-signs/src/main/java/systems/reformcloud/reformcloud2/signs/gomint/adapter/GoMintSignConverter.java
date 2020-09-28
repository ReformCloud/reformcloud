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
package systems.reformcloud.reformcloud2.signs.gomint.adapter;

import io.gomint.GoMint;
import io.gomint.math.BlockPosition;
import io.gomint.world.World;
import io.gomint.world.block.Block;
import io.gomint.world.block.BlockSign;
import org.apache.commons.math3.util.FastMath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.reformcloud.reformcloud2.signs.util.converter.SignConverter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudLocation;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

public class GoMintSignConverter implements SignConverter<BlockSign> {

    protected static final SignConverter<BlockSign> INSTANCE = new GoMintSignConverter();

    private GoMintSignConverter() {
    }

    @Nullable
    @Override
    public BlockSign from(@NotNull CloudSign cloudSign) {
        World world = GoMint.instance().getWorld(cloudSign.getLocation().getWorld());
        if (world == null) {
            return null;
        }

        Block block = world.getBlockAt(toInt(cloudSign.getLocation().getX()), toInt(cloudSign.getLocation().getY()), toInt(cloudSign.getLocation().getZ()));
        return block instanceof BlockSign ? (BlockSign) block : null;
    }

    @Override
    public @NotNull CloudSign to(@NotNull BlockSign blockSign, @NotNull String group) {
        return new CloudSign(group, this.to(blockSign));
    }

    @Override
    public @NotNull CloudLocation to(@NotNull BlockSign blockSign) {
        final BlockPosition position = blockSign.getPosition();
        return new CloudLocation(
            blockSign.getWorld().getWorldName(),
            Embedded.getInstance().getCurrentProcessInformation().getProcessGroup().getName(),
            position.getX(),
            position.getY(),
            position.getZ(),
            0,
            0
        );
    }

    private static int toInt(double d) {
        return FastMath.toIntExact(FastMath.round(d));
    }
}
