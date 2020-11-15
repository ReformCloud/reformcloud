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

import com.google.inject.Inject;
import org.cloudburstmc.server.event.Listener;
import org.cloudburstmc.server.event.player.PlayerChatEvent;
import org.cloudburstmc.server.plugin.Dependency;
import org.cloudburstmc.server.plugin.Plugin;
import org.cloudburstmc.server.utils.Config;
import org.cloudburstmc.server.utils.ConfigSection;
import org.cloudburstmc.server.utils.TextFormat;

import java.nio.file.Files;
import java.nio.file.Path;
@Plugin(
    id = "reformcloud_2_chat",
    name = "CloudBurstChatPlugin",
    version = "2",
    description = "The reformcloud chat prefix plugin",
    url = "https://reformcloud.systems",
    authors = {"derklaro"},
    dependencies = {@Dependency(id = "reformcloud_2_perms")}
)
public class CloudBurstChatPlugin {

    private final String chatFormat;

    @Inject
    public CloudBurstChatPlugin() {
        Path path = Path.of("plugins/ReformCloud2Chat/config.yml").toAbsolutePath();
        if (Files.notExists(path)) {
            new Config(path.toString(), Config.YAML, new ConfigSection("format", "%display%%name% &7➤ &f%message%"));
        }

        this.chatFormat = new Config(path.toString(), Config.YAML).getString("format", "%display%%name% &7➤ &f%message%");
    }

    @Listener
    public void handle(PlayerChatEvent event) {
        String format = ChatFormatUtil.buildFormat(
            event.getPlayer().getServerId(),
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
