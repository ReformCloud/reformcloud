package systems.reformcloud.reformcloud2.permissions.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.permissions.bukkit.permissible.DefaultPermissible;

import java.lang.reflect.Field;

public class BukkitUtil {

    private BukkitUtil() {
        throw new UnsupportedOperationException();
    }

    private static Field PERM_FIELD;

    static {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

            try {
                // bukkit
                PERM_FIELD = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftHumanEntity").getDeclaredField("perm");
                PERM_FIELD.setAccessible(true);
            } catch (final Throwable throwable) {
                // glowstone
                PERM_FIELD = Class.forName("net.glowstone.entity.GlowHumanEntity").getDeclaredField("permissions");
                PERM_FIELD.setAccessible(true);
            }
        } catch (final Throwable throwable) {
            throw new RuntimeException("Error while obtaining bukkit or glowstone perm fields (are you using your own build?)", throwable);
        }
    }

    public static void injectPlayer(Player player) {
        Conditions.isTrue(player != null);
        Conditions.isTrue(PERM_FIELD != null);

        try {
            PERM_FIELD.set(player, new DefaultPermissible(player));
        } catch (final IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }
}
