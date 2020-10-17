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

import io.gomint.ChatColor;
import io.gomint.GoMint;
import io.gomint.entity.EntityPlayer;
import io.gomint.math.Location;
import io.gomint.math.Vector;
import io.gomint.plugin.Plugin;
import io.gomint.world.block.Block;
import io.gomint.world.block.BlockSign;
import io.gomint.world.block.BlockStandingSign;
import io.gomint.world.block.BlockWallSign;
import io.gomint.world.block.data.Facing;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.signs.SharedSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.gomint.commands.GoMintCommandSigns;
import systems.reformcloud.reformcloud2.signs.gomint.listener.GoMintListener;
import systems.reformcloud.reformcloud2.signs.gomint.util.Ticks;
import systems.reformcloud.reformcloud2.signs.util.PlaceHolderUtil;
import systems.reformcloud.reformcloud2.signs.util.converter.SignConverter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignSubLayout;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GoMintSignSystemAdapter extends SharedSignSystemAdapter<BlockSign> {

    protected final Collection<ScheduledFuture<?>> startedTasks = new CopyOnWriteArrayList<>();

    public GoMintSignSystemAdapter(@NotNull SignConfig signConfig, @NotNull Plugin plugin) {
        super(signConfig);

        GoMint.instance().getPluginManager().registerListener(plugin, new GoMintListener(this));
        GoMint.instance().getPluginManager().registerCommand(plugin, new GoMintCommandSigns(this));
    }

    @Override
    protected void setSignLines(@NotNull CloudSign cloudSign, @NotNull String[] lines) {
        BlockSign blockSign = this.getSignConverter().from(cloudSign);
        if (blockSign != null && lines.length == 4) {
            for (int i = 0; i < 4; i++) {
                blockSign.setLine(i + 1, lines[i]);
            }
        }
    }

    @Override
    protected void runTasks() {
        this.startedTasks.add(CommonHelper.SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(
            this::updateSigns,
            0,
            Ticks.ms(this.signConfig.getUpdateInterval()),
            TimeUnit.MILLISECONDS
        ));

        this.startedTasks.add(CommonHelper.SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            for (CloudSign sign : this.signs) {
                BlockSign blockSign = this.getSignConverter().from(sign);
                if (blockSign == null) {
                    continue;
                }

                final Vector signVector = blockSign.getPosition().toVector();
                for (EntityPlayer player : blockSign.getWorld().getPlayers()) {
                    if (player.getPermissionManager().hasPermission(this.signConfig.getKnockBackBypassPermission())) {
                        continue;
                    }

                    final Location location = player.getLocation();
                    if (this.signConfig.getKnockBackDistance() >= location.distance(signVector)) {
                        Vector vector = player.getLocation()
                            .subtract(signVector)
                            .normalize()
                            .multiply((float) this.signConfig.getKnockBackStrength());
                        vector.setY(0.2F);
                        player.setVelocity(vector);
                    }
                }
            }
        }, Ticks.ms(20), Ticks.ms(5), TimeUnit.MILLISECONDS));
    }

    @Override
    protected @NotNull String replaceAll(@NotNull String line, @NotNull String group, @Nullable ProcessInformation processInformation) {
        if (processInformation == null) {
            line = line.replace("%group%", group);
            return ChatColor.translateAlternateColorCodes('&', line);
        }

        return PlaceHolderUtil.format(line, group, processInformation, s -> ChatColor.translateAlternateColorCodes('&', s));
    }

    @Override
    public void changeBlock(@NotNull CloudSign sign, @NotNull SignSubLayout layout) {
        BlockSign blockSign = this.getSignConverter().from(sign);
        if (blockSign != null) {
            this.changeBackBlock(blockSign, layout);
        }
    }

    @Override
    public @NotNull SignConverter<BlockSign> getSignConverter() {
        return GoMintSignConverter.INSTANCE;
    }

    @Override
    public void handleSignConfigUpdate(@NotNull SignConfig config) {
        for (ScheduledFuture<?> startedTask : this.startedTasks) {
            startedTask.cancel(true);
        }

        this.startedTasks.clear();
        super.signConfig = config;
        this.runTasks();
    }

    @SuppressWarnings("unchecked")
    private void changeBackBlock(@NotNull BlockSign blockSign, @NotNull SignSubLayout layout) {
        final Facing facing;
        if (blockSign instanceof BlockWallSign) {
            facing = ((BlockWallSign) blockSign).getFacing();
        } else if (blockSign instanceof BlockStandingSign) {
            facing = Facing.DOWN; // Sign stands so the only possible facing is down
        } else {
            throw new IllegalStateException("Unknown sign type " + blockSign.getClass().getName());
        }

        Block behind = blockSign.getSide(facing.opposite());
        if (behind == null) {
            return;
        }

        Class<? extends Block> targetBlockClass;
        try {
            Class<?> clazz = Class.forName("io.gomint.world.Block" + layout.getBlock());
            if (Block.class.isAssignableFrom(clazz)) {
                targetBlockClass = (Class<? extends Block>) clazz;
            } else {
                System.err.println("Unable to find block of type " + layout.getBlock());
                return;
            }
        } catch (ClassNotFoundException | ClassCastException exception) {
            System.err.println("Unable to find block of type " + layout.getBlock());
            return;
        }

        behind.setBlockType(targetBlockClass);
    }
}
