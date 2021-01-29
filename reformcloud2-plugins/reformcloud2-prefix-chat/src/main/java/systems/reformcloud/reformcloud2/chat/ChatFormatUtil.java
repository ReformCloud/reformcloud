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
package systems.reformcloud.reformcloud2.chat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.permissions.PermissionManagement;
import systems.reformcloud.reformcloud2.permissions.objects.PermissionGroup;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

final class ChatFormatUtil {

  private ChatFormatUtil() {
    throw new UnsupportedOperationException();
  }

  @Nullable
  static String buildFormat(@NotNull UUID playerUniqueId, @NotNull String format, @NotNull String message,
                            @NotNull String playerName, @NotNull String playerDisplayName,
                            @NotNull Function<String, Boolean> permissionChecker, @NotNull BiFunction<Character, String, String> colourReplacer) {
    return PermissionManagement.getInstance().getExistingUser(playerUniqueId).map(user -> {
      final String usableMessage = permissionChecker.apply("reformcloud.chat.coloured")
        ? colourReplacer.apply('&', message.replace("%", "%%"))
        : message.replace("%", "%%");
      if (usableMessage.trim().isEmpty()) {
        return null;
      }

      PermissionGroup permissionGroup = user.getHighestPermissionGroup().orElse(null);
      final String finalFormat = format
        .replace("%name%", playerName)
        .replace("%player_display%", playerDisplayName)
        .replace("%group%", permissionGroup == null ? "" : permissionGroup.getName())
        .replace("%priority%", permissionGroup == null ? "" : Integer.toString(permissionGroup.getPriority()))
        .replace("%prefix%", user.getPrefix().orElse(""))
        .replace("%suffix%", user.getSuffix().orElse(""))
        .replace("%display%", user.getDisplay().orElse(""))
        .replace("%colour%", user.getColour().orElse(""));
      return colourReplacer.apply('&', finalFormat).replace("%message%", usableMessage);
    }).orElse(null);
  }
}
