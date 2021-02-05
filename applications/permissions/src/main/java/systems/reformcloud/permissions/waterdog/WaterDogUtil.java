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
package systems.reformcloud.permissions.waterdog;

import dev.waterdog.player.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.permissions.waterdog.map.PermissionObject2ObjectOpenHashMap;

import java.lang.reflect.Field;

public final class WaterDogUtil {

  private static final Field PERM_MAP;

  static {
    try {
      PERM_MAP = ProxiedPlayer.class.getDeclaredField("permissions");
      PERM_MAP.setAccessible(true);
    } catch (NoSuchFieldException exception) {
      throw new RuntimeException(exception);
    }
  }

  private WaterDogUtil() {
    throw new UnsupportedOperationException();
  }

  public static void inject(@NotNull ProxiedPlayer player) {
    try {
      PERM_MAP.set(player, new PermissionObject2ObjectOpenHashMap(player.getUniqueId()));
    } catch (IllegalAccessException exception) {
      exception.printStackTrace();
    }
  }
}
