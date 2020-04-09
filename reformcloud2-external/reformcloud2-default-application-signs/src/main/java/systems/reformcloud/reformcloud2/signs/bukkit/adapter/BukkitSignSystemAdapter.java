package systems.reformcloud.reformcloud2.signs.bukkit.adapter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.signs.SharedSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.bukkit.commands.BukkitCommandSigns;
import systems.reformcloud.reformcloud2.signs.bukkit.listener.BukkitListener;
import systems.reformcloud.reformcloud2.signs.util.PlaceHolderUtil;
import systems.reformcloud.reformcloud2.signs.util.converter.SignConverter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignSubLayout;

import javax.annotation.Nonnull;
import java.util.Optional;

public class BukkitSignSystemAdapter extends SharedSignSystemAdapter<Sign> {

    private static BukkitSignSystemAdapter instance;

    public BukkitSignSystemAdapter(JavaPlugin plugin, SignConfig config) {
        super(config);
        instance = this;

        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(new BukkitListener(), plugin);

        PluginCommand signs = plugin.getCommand("signs");
        Conditions.isTrue(signs != null);
        signs.setExecutor(new BukkitCommandSigns());
        signs.setPermission("reformcloud.command.signs");

        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    }

    private final Plugin plugin;

    private int taskID = -1;

    private int knockBackTaskID = -1;

    @Override
    protected void setSignLines(@Nullable Sign sign, @NotNull String[] lines) {
        if (sign != null && lines.length == 4) {
            for (int i = 0; i < 4; i++) {
                sign.setLine(i, lines[i]);
            }

            sign.update();
        }
    }

    @Override
    public void handleSignConfigUpdate(@NotNull SignConfig config) {
        super.signConfig = config;
        this.restartTask();
    }

    @Override
    public @NotNull SignConverter<Sign> getSignConverter() {
        return BukkitSignConverter.INSTANCE;
    }

    @Nonnull
    protected String replaceAll(@Nonnull String line, @Nonnull String group, ProcessInformation processInformation) {
        if (processInformation == null) {
            line = line.replace("%group%", group);
            return ChatColor.translateAlternateColorCodes('&', line);
        }

        return PlaceHolderUtil.format(line, group, processInformation, s -> ChatColor.translateAlternateColorCodes('&', s));
    }

    @Override
    public void changeBlock(@NotNull CloudSign sign, @NotNull SignSubLayout layout) {
        Sign bukkit = this.getSignConverter().from(sign);
        if (bukkit != null) {
            if (Bukkit.isPrimaryThread()) {
                this.changeBlockBehind(bukkit, layout);
            } else {
                Bukkit.getScheduler().runTask(this.plugin, () -> this.changeBlockBehind(bukkit, layout));
            }
        }
    }

    private void changeBlockBehind(Sign sign, SignSubLayout layout) {
        BlockFace blockFace = null;

        try {
            org.bukkit.material.Sign signData = (org.bukkit.material.Sign) sign.getData();
            if (signData.isWallSign()) {
                blockFace = signData.getFacing();
            }
        } catch (final Throwable throwable) {
            if (sign.getBlockData() instanceof Directional) {
                Directional directional = (Directional) sign.getBlockData();
                blockFace = directional.getFacing();
            }
        }

        getRelative(blockFace).ifPresent(e -> {
            Material material = Material.getMaterial(layout.getBlock());
            if (material == null) {
                return;
            }

            BlockState back = sign.getBlock().getRelative(e).getState();
            back.setType(material);
            back.setData(new MaterialData(material, (byte) layout.getSubID()));
            back.update(true);
        });
    }

    private Optional<BlockFace> getRelative(BlockFace face) {
        if (face == null) {
            return Optional.empty();
        }

        switch (face) {
            case EAST: {
                return Optional.of(BlockFace.WEST);
            }

            case WEST: {
                return Optional.of(BlockFace.EAST);
            }

            case NORTH: {
                return Optional.of(BlockFace.SOUTH);
            }

            case SOUTH: {
                return Optional.of(BlockFace.NORTH);
            }
        }

        return Optional.empty();
    }

    private void restartTask() {
        if (taskID != -1) {
            Bukkit.getScheduler().cancelTask(taskID);
            taskID = -1;
        }

        if (knockBackTaskID != -1) {
            Bukkit.getScheduler().cancelTask(knockBackTaskID);
            knockBackTaskID = -1;
        }

        runTasks();
    }

    @Override
    protected void runTasks() {
        taskID = Bukkit.getScheduler().runTaskTimer(plugin, this::updateSigns, 0, 20 * super.signConfig.getUpdateInterval()).getTaskId();

        double distance = super.signConfig.getKnockBackDistance();
        knockBackTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (CloudSign cachedSign : this.signs) {
                Sign bukkitSign = this.getSignConverter().from(cachedSign);
                if (bukkitSign == null) {
                    continue;
                }

                Location location = bukkitSign.getLocation();
                if (location.getWorld() == null) {
                    continue;
                }

                location.getWorld()
                        .getNearbyEntities(location, distance, distance, distance)
                        .stream()
                        .filter(e -> e instanceof Player && !e.hasPermission(super.signConfig.getKnockBackBypassPermission()))
                        .forEach(e -> e.setVelocity(e.getLocation()
                                .toVector()
                                .subtract(location.toVector())
                                .normalize()
                                .multiply(super.signConfig.getKnockBackStrength())
                                .setY(0.2D)
                        ));
            }
        }, 20, 5);
    }

    public static BukkitSignSystemAdapter getInstance() {
        return instance;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
