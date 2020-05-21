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
package systems.reformcloud.reformcloud2.permissions.bukkit.vault;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.permissions.PermissionManagement;
import systems.reformcloud.reformcloud2.permissions.internal.UUIDFetcher;
import systems.reformcloud.reformcloud2.permissions.objects.user.PermissionUser;

import java.util.Optional;
import java.util.UUID;

public final class VaultUtil {

    private VaultUtil() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static Optional<PermissionUser> getUserFromName(@NotNull String name) {
        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            return PermissionManagement.getInstance().getExistingUser(player.getUniqueId());
        }

        UUID uniqueID = UUIDFetcher.getUUIDFromName(name);
        if (uniqueID != null) {
            return PermissionManagement.getInstance().getExistingUser(uniqueID);
        }

        return Optional.empty();
    }

    public static void tryInvoke(@NotNull Plugin plugin) {
        try {
            ServicesManager servicesManager = Bukkit.getServicesManager();
            Permission permission = new PermissionVaultPermissionImplementation();

            servicesManager.register(Permission.class, permission, plugin, ServicePriority.Highest);
            servicesManager.register(Chat.class, new PermissionVaultChatImplementation(permission), plugin, ServicePriority.Highest);
        } catch (final Throwable ignored) {
        }
    }
}
