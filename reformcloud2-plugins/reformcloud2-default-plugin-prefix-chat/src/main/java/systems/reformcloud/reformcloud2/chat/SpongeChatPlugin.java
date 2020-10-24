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
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(
    id = "reformcloud_2_chat",
    name = "ReformCloud2Chat",
    version = "2",
    description = "The reformcloud chat plugin",
    authors = {"derklaro"},
    url = "https://reformcloud.systems",
    dependencies = {@Dependency(id = "reformcloud_2_perms")}
)
public class SpongeChatPlugin {

    @Inject
    @DefaultConfig(sharedRoot = false)
    public Path defaultConf;

    private String chatFormat;

    @Listener
    public void handle(GameInitializationEvent event) {
        ConfigurationLoader<CommentedConfigurationNode> configurationLoader = HoconConfigurationLoader.builder().setPath(this.defaultConf).build();
        try {
            CommentedConfigurationNode configurationNode = configurationLoader.load();
            if (Files.notExists(this.defaultConf)) {
                Files.createFile(this.defaultConf);
                // set defaults in config
                configurationNode.getNode("format").setValue("%display%%name% &7➤ &f%message%");
                configurationLoader.save(configurationNode);
            }

            CommentedConfigurationNode format = configurationNode.getNode("format");
            if (format.isVirtual()) {
                // the node is not set in the config/was removed
                format.setValue("%display%%name% &7➤ &f%message%");
                configurationLoader.save(configurationNode);
            }

            this.chatFormat = format.getString();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Listener
    public void handle(MessageChannelEvent.Chat event) {
        event.getCause().first(Player.class).ifPresent(player -> {
            String format = ChatFormatUtil.buildFormat(
                player.getUniqueId(),
                this.chatFormat,
                event.getRawMessage().toPlain(),
                player.getName(),
                player.getDisplayNameData().displayName().get().toPlain(),
                player::hasPermission,
                (colorChar, message) -> TextSerializers.FORMATTING_CODE.replaceCodes(message, colorChar)
            );
            if (format == null) {
                event.setCancelled(true);
            } else {
                event.setMessage(Text.of(format));
            }
        });
    }
}
