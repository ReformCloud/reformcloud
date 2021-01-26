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

import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;

public class NukkitChatPlugin extends PluginBase implements Listener {

  private String chatFormat;

  @Override
  public void onEnable() {
    if (Server.getInstance().getPluginManager().getPlugin("ReformCloud2NukkitPermissions") == null) {
      System.err.println("[Chat] Unable to find permission plugin, do not load permission listeners");
      return;
    }

    super.saveDefaultConfig();
    this.chatFormat = super.getConfig().getString("format", "%display%%name% &7➤ &f%message%");

    Server.getInstance().getPluginManager().registerEvents(this, this);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void handle(PlayerChatEvent event) {
    String format = ChatFormatUtil.buildFormat(
      event.getPlayer().getUniqueId(),
      this.chatFormat,
      event.getMessage(),
      event.getPlayer().getName(),
      event.getPlayer().getDisplayName(),
      event.getPlayer()::hasPermission,
      (colorChar, message) -> TextFormat.colorize(colorChar, message)
    );
    if (format == null) {
      event.setCancelled(true);
    } else {
      event.setFormat(format);
    }
  }
}
