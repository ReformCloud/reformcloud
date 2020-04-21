package systems.reformcloud.reformcloud2.signs.bukkit.adapter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
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

import java.lang.reflect.Method;

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

    @Override
    protected void setSignLines(@NotNull CloudSign cloudSign, @NotNull String[] lines) {
        this.run(() -> {
            Sign sign = this.getSignConverter().from(cloudSign);
            if (sign != null && lines.length == 4) {
                for (int i = 0; i < 4; i++) {
                    sign.setLine(i, lines[i]);
                }

                sign.update();
            }
        });
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

    @NotNull
    protected String replaceAll(@NotNull String line, @NotNull String group, ProcessInformation processInformation) {
        if (processInformation == null) {
            line = line.replace("%group%", group);
            return ChatColor.translateAlternateColorCodes('&', line);
        }

        return PlaceHolderUtil.format(line, group, processInformation, s -> ChatColor.translateAlternateColorCodes('&', s));
    }

    @Override
    public void changeBlock(@NotNull CloudSign sign, @NotNull SignSubLayout layout) {
        this.run(() -> {
            Sign bukkit = this.getSignConverter().from(sign);
            if (bukkit != null) {
                this.changeBlockBehind(bukkit, layout);
            }
        });
    }

    @Override
    public void cleanSigns() {
        this.run(() -> super.cleanSigns());
    }

    private void changeBlockBehind(@NotNull Sign sign, @NotNull SignSubLayout layout) {
        BlockState blockState = sign.getLocation().getBlock().getState();
        BlockFace blockFace = this.getSignFacing(blockState);

        if (blockFace == null) {
            MaterialData data = blockState.getData();
            if (data instanceof org.bukkit.material.Sign) {
                org.bukkit.material.Sign materialSign = (org.bukkit.material.Sign) data;
                blockFace = materialSign.isWallSign() ? materialSign.getFacing() : BlockFace.UP;
            }
        }

        if (blockFace == null) {
            return;
        }

        BlockState back = sign.getLocation().getBlock().getRelative(blockFace.getOppositeFace()).getState();
        Material material = Material.getMaterial(layout.getBlock().toUpperCase());
        if (material == null || !material.isBlock()) {
            return;
        }

        back.setType(material);
        if (layout.getSubID() > -1) {
            back.setData(new MaterialData(material, (byte) layout.getSubID()));
        }
        back.update(true);
    }

    @Nullable
    private BlockFace getSignFacing(@NotNull BlockState blockState) {
        try {
            Method getBlockDataMethod = BlockState.class.getDeclaredMethod("getBlockData");
            Object blockData = getBlockDataMethod.invoke(blockState);

            Class<?> wallSignClass = Class.forName("org.bukkit.block.data.type.WallSign");
            if (wallSignClass.isInstance(blockData)) {
                Method getFacingMethod = wallSignClass.getMethod("getFacing");
                return (BlockFace) getFacingMethod.invoke(blockData);
            }

            return BlockFace.UP;
        } catch (final ReflectiveOperationException ex) {
            return null;
        }
    }

    private void restartTask() {
        Bukkit.getScheduler().cancelTasks(this.plugin);
        runTasks();
    }

    @Override
    protected void runTasks() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::updateSigns, 0, 20 * super.signConfig.getUpdateInterval());

        double distance = super.signConfig.getKnockBackDistance();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
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

    private void run(@NotNull Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Bukkit.getScheduler().runTask(this.plugin, runnable);
        }
    }

    public static BukkitSignSystemAdapter getInstance() {
        return instance;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
