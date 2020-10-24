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
package systems.reformcloud.reformcloud2.signs.cloudburst.adapter;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import org.cloudburstmc.server.Server;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockIds;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.block.BlockTraits;
import org.cloudburstmc.server.block.behavior.BlockBehaviorSignPost;
import org.cloudburstmc.server.blockentity.Sign;
import org.cloudburstmc.server.entity.Entity;
import org.cloudburstmc.server.math.AxisAlignedBB;
import org.cloudburstmc.server.math.SimpleAxisAlignedBB;
import org.cloudburstmc.server.player.Player;
import org.cloudburstmc.server.utils.Identifier;
import org.cloudburstmc.server.utils.TextFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.signs.SharedSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.cloudburst.listener.CloudBurstListener;
import systems.reformcloud.reformcloud2.signs.util.PlaceHolderUtil;
import systems.reformcloud.reformcloud2.signs.util.converter.SignConverter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignSubLayout;

import java.lang.reflect.Field;

public class CloudBurstSignSystemAdapter extends SharedSignSystemAdapter<Sign> {

    private static CloudBurstSignSystemAdapter instance;
    private final Object plugin;

    public CloudBurstSignSystemAdapter(@NotNull SignConfig signConfig, @NotNull Object plugin) {
        super(signConfig);

        instance = this;
        this.plugin = plugin;

        Server.getInstance().getEventManager().registerListeners(plugin, new CloudBurstListener());
    }

    public static CloudBurstSignSystemAdapter getInstance() {
        return instance;
    }

    @Override
    protected void setSignLines(@NotNull CloudSign cloudSign, @NotNull String[] lines) {
        Sign blockEntitySign = this.getSignConverter().from(cloudSign);
        if (blockEntitySign == null) {
            return;
        }

        blockEntitySign.setText(lines);
    }

    @Override
    protected void runTasks() {
        Server.getInstance().getScheduler().scheduleRepeatingTask(
            this.plugin, this::updateSigns, CommonHelper.longToInt(Math.round(20 / super.signConfig.getUpdateInterval()))
        );

        float distance = (float) super.signConfig.getKnockBackDistance();
        Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(this.plugin, () -> {
            for (CloudSign sign : this.signs) {
                Sign blockEntitySign = this.getSignConverter().from(sign);
                if (blockEntitySign == null) {
                    continue;
                }

                Vector3i vector3i = blockEntitySign.getPosition();
                AxisAlignedBB alignedBB = new SimpleAxisAlignedBB(vector3i, vector3i)
                    .expand(distance, distance, distance);

                for (Entity entity : blockEntitySign.getLevel().getNearbyEntities(alignedBB)) {
                    if (!(entity instanceof Player)) {
                        continue;
                    }

                    Player player = (Player) entity;
                    if (player.hasPermission(super.signConfig.getKnockBackBypassPermission())) {
                        continue;
                    }

                    Vector3f vector = player.getPosition()
                        .sub(vector3i.toFloat())
                        .normalize()
                        .mul(super.signConfig.getKnockBackStrength());
                    player.setMotion(Vector3f.from(vector.getX(), 0.2D, vector.getZ()));
                }
            }
        }, 20, 5);
    }

    @Override
    protected @NotNull String replaceAll(@NotNull String line, @NotNull String group, @Nullable ProcessInformation processInformation) {
        if (processInformation == null) {
            line = line.replace("%group%", group);
            return TextFormat.colorize('&', line);
        }

        return PlaceHolderUtil.format(line, group, processInformation, s -> TextFormat.colorize('&', s));
    }

    @Override
    public void changeBlock(@NotNull CloudSign sign, @NotNull SignSubLayout layout) {
        Sign blockEntitySign = this.getSignConverter().from(sign);
        if (blockEntitySign == null) {
            return;
        }

        this.changeBlock0(blockEntitySign, layout);
    }

    @Override
    public @NotNull SignConverter<Sign> getSignConverter() {
        return CloudBurstSignConverter.INSTANCE;
    }

    @Override
    public void handleSignConfigUpdate(@NotNull SignConfig config) {
        super.signConfig = config;
        this.restartTasks();
    }

    private void changeBlock0(@NotNull Sign sign, @NotNull SignSubLayout layout) {
        if (!(sign.getBlock().getState().getBehavior() instanceof BlockBehaviorSignPost)) {
            return;
        }

        Block behind = sign.getBlock().getSide(sign.getBlockState().ensureTrait(BlockTraits.FACING_DIRECTION).getOpposite());
        try {
            Field blockType = BlockIds.class.getDeclaredField(layout.getBlock().toUpperCase());
            blockType.setAccessible(true);
            sign.getLevel().setBlock(behind.getPosition(), BlockState.get((Identifier) blockType.get(null)), true, true);
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException ignored) {
        }
    }

    private void restartTasks() {
        Server.getInstance().getScheduler().cancelTask(this.plugin);
        this.runTasks();
    }
}
