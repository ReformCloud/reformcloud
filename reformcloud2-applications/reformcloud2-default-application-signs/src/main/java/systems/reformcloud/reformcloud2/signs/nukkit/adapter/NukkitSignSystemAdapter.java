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
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockSignPost;
import cn.nukkit.blockentity.Sign;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.player.Player;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.registry.RegistryException;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.TextFormat;
import com.nukkitx.math.vector.Vector3f;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.signs.SharedSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.nukkit.commands.NukkitCommandSigns;
import systems.reformcloud.reformcloud2.signs.nukkit.listener.NukkitListener;
import systems.reformcloud.reformcloud2.signs.util.PlaceHolderUtil;
import systems.reformcloud.reformcloud2.signs.util.converter.SignConverter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignSubLayout;

public class NukkitSignSystemAdapter extends SharedSignSystemAdapter<Sign> {

    private static NukkitSignSystemAdapter instance;
    private final PluginBase plugin;

    public NukkitSignSystemAdapter(@NotNull SignConfig signConfig, @NotNull PluginBase plugin) {
        super(signConfig);

        instance = this;
        this.plugin = plugin;

        Server.getInstance().getPluginManager().registerEvents(new NukkitListener(), plugin);

        PluginCommand<Plugin> command = plugin.getCommand("signs");
        command.setExecutor(new NukkitCommandSigns());
        command.getPermissions().add("reformcloud.command.signs");
    }

    public static NukkitSignSystemAdapter getInstance() {
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

                Location location = Location.from(blockEntitySign.getPosition(), blockEntitySign.getLevel());
                AxisAlignedBB alignedBB = new SimpleAxisAlignedBB(blockEntitySign.getPosition(), blockEntitySign.getPosition())
                        .expand(distance, distance, distance);

                for (Entity entity : location.getLevel().getNearbyEntities(alignedBB)) {
                    if (!(entity instanceof Player)) {
                        continue;
                    }

                    Player player = (Player) entity;
                    if (player.hasPermission(super.signConfig.getKnockBackBypassPermission())) {
                        continue;
                    }

                    Vector3f vector = player.getPosition()
                            .sub(location.getX(), location.getY(), location.getZ())
                            .normalize()
                            .mul(super.signConfig.getKnockBackStrength());
                    player.setMotion(Vector3f.from(vector.getX(), 0.2f, vector.getZ()));
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
        return NukkitSignConverter.INSTANCE;
    }

    @Override
    public void handleSignConfigUpdate(@NotNull SignConfig config) {
        super.signConfig = config;
        this.restartTasks();
    }

    private void changeBlock0(@NotNull Sign sign, @NotNull SignSubLayout layout) {
        if (!(sign.getBlock() instanceof BlockSignPost)) {
            return;
        }

        BlockSignPost post = (BlockSignPost) sign.getBlock();
        Location location = this.getLocationFromBlock(post.getSide(post.getBlockFace().getOpposite()));
        Block block = this.getBlockByName(layout.getBlock());
        if (block == null) {
            return;
        }

        location.getLevel().setBlock(location.getPosition().toInt(), block, true, true);
    }

    private void restartTasks() {
        Server.getInstance().getScheduler().cancelTask(this.plugin);
        this.runTasks();
    }

    private @Nullable Block getBlockByName(@NotNull String name) {
        try {
            Identifier identifier = (Identifier) BlockIds.class.getField(name.toUpperCase()).get(null);
            return Block.get(identifier);
        } catch (NoSuchFieldException | IllegalAccessException | RegistryException exception) {
            return null;
        }
    }

    private @NotNull Location getLocationFromBlock(@NotNull Block block) {
        return Location.from(block.getPosition(), block.getLevel());
    }
}
