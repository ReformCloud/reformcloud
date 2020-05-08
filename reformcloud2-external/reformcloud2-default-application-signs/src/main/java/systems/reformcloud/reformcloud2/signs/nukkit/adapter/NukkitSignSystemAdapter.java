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

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSignPost;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.annotiations.UndefinedNullability;
import systems.reformcloud.reformcloud2.signs.SharedSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.nukkit.commands.NukkitCommandSigns;
import systems.reformcloud.reformcloud2.signs.nukkit.listener.NukkitListener;
import systems.reformcloud.reformcloud2.signs.util.PlaceHolderUtil;
import systems.reformcloud.reformcloud2.signs.util.converter.SignConverter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignSubLayout;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NukkitSignSystemAdapter extends SharedSignSystemAdapter<BlockEntitySign> {

    private static final Map<String, Integer> BLOCKS = new ConcurrentHashMap<>();

    static {
        Arrays.stream(Block.fullList).forEach(e -> BLOCKS.put(e.getName(), e.getId()));
    }

    public NukkitSignSystemAdapter(@NotNull SignConfig signConfig, @NotNull PluginBase plugin) {
        super(signConfig);

        instance = this;
        this.plugin = plugin;

        Server.getInstance().getPluginManager().registerEvents(new NukkitListener(), plugin);

        PluginCommand command = (PluginCommand) plugin.getCommand("signs");
        command.setExecutor(new NukkitCommandSigns());
        command.setPermission("reformcloud.command.signs");
    }

    private final PluginBase plugin;

    private static NukkitSignSystemAdapter instance;

    @Override
    protected void setSignLines(@NotNull CloudSign cloudSign, @NotNull String[] lines) {
        BlockEntitySign blockEntitySign = this.getSignConverter().from(cloudSign);
        if (blockEntitySign == null) {
            return;
        }

        blockEntitySign.setText(lines);
    }

    @Override
    protected void runTasks() {
        Server.getInstance().getScheduler().scheduleRepeatingTask(
                plugin, this::updateSigns, CommonHelper.longToInt(super.signConfig.getUpdateInterval() * 20)
        );

        double distance = super.signConfig.getKnockBackDistance();
        Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(this.plugin, () -> {
            for (CloudSign sign : this.signs) {
                BlockEntitySign blockEntitySign = this.getSignConverter().from(sign);
                if (blockEntitySign == null) {
                    continue;
                }

                Location location = blockEntitySign.getLocation();
                AxisAlignedBB alignedBB = new SimpleAxisAlignedBB(location, location)
                        .expand(distance, distance, distance);

                for (Entity entity : location.getLevel().getNearbyEntities(alignedBB)) {
                    if (!(entity instanceof Player)) {
                        continue;
                    }

                    Player player = (Player) entity;
                    if (player.hasPermission(super.signConfig.getKnockBackBypassPermission())) {
                        continue;
                    }

                    Vector3 vector = player.getPosition()
                            .subtract(location)
                            .normalize()
                            .multiply(super.signConfig.getKnockBackStrength());
                    vector.y = 0.2D;
                    player.setMotion(vector);
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
        BlockEntitySign blockEntitySign = this.getSignConverter().from(sign);
        if (blockEntitySign == null) {
            return;
        }

        this.changeBlock0(blockEntitySign, layout);
    }

    @Override
    public @NotNull SignConverter<BlockEntitySign> getSignConverter() {
        return NukkitSignConverter.INSTANCE;
    }

    @Override
    public void handleSignConfigUpdate(@NotNull SignConfig config) {
        super.signConfig = config;
        this.restartTasks();
    }

    @UndefinedNullability
    public static NukkitSignSystemAdapter getInstance() {
        return instance;
    }

    private void changeBlock0(@NotNull BlockEntitySign sign, @NotNull SignSubLayout layout) {
        if (!(sign.getBlock() instanceof BlockSignPost)) {
            return;
        }

        BlockSignPost post = (BlockSignPost) sign.getBlock();
        Location location = post.getSide(post.getBlockFace().getOpposite()).getLocation();
        Integer block = BLOCKS.get(layout.getBlock());
        if (block == null) {
            return;
        }

        Block nukkitBlock = Block.fullList[(block << 4) + layout.getSubID()].clone();
        location.getLevel().setBlock(location, nukkitBlock, true, true);
    }

    private void restartTasks() {
        Server.getInstance().getScheduler().cancelTask(this.plugin);
        this.runTasks();
    }
}
