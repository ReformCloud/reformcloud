/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
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
package systems.reformcloud.permissions.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.base.Conditions;
import systems.reformcloud.permissions.bukkit.permissible.DefaultPermissible;

import java.lang.reflect.Field;

public final class BukkitUtil {

  private static Field perm;

  static {
    try {
      try {
        // bukkit
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        perm = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftHumanEntity").getDeclaredField("perm");
        perm.setAccessible(true);
      } catch (final Throwable ex) {
        // glowstone
        perm = Class.forName("net.glowstone.entity.GlowHumanEntity").getDeclaredField("permissions");
        perm.setAccessible(true);
      }
    } catch (final ClassNotFoundException | NoSuchFieldException ex) {
      throw new RuntimeException("Error while obtaining bukkit or glowstone perm fields (are you using your own build?)", ex);
    }
  }

  private BukkitUtil() {
    throw new UnsupportedOperationException();
  }

  public static void injectPlayer(@NotNull Player player) {
    Conditions.isTrue(perm != null);

    try {
      perm.set(player, new DefaultPermissible(player));
    } catch (final IllegalAccessException ex) {
      ex.printStackTrace();
    }
  }
}
