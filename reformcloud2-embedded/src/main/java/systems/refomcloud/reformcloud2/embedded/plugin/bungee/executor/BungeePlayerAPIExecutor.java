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
package systems.refomcloud.reformcloud2.embedded.plugin.bungee.executor;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import systems.refomcloud.reformcloud2.embedded.executor.PlayerAPIExecutor;

import java.util.UUID;

public class BungeePlayerAPIExecutor extends PlayerAPIExecutor {

    @Override
    public void executeSendMessage(UUID player, String message) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        if (proxiedPlayer != null) {
            proxiedPlayer.sendMessage(TextComponent.fromLegacyText(message));
        }
    }

    @Override
    public void executeKickPlayer(UUID player, String message) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        if (proxiedPlayer != null) {
            proxiedPlayer.disconnect(TextComponent.fromLegacyText(message));
        }
    }

    @Override
    public void executePlaySound(UUID player, String sound, float f1, float f2) {
    }

    @Override
    public void executeSendTitle(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        if (proxiedPlayer != null) {
            ProxyServer.getInstance().createTitle()
                .title(TextComponent.fromLegacyText(title))
                .subTitle(TextComponent.fromLegacyText(subTitle))
                .fadeIn(fadeIn)
                .stay(stay)
                .fadeOut(fadeOut)
                .send(proxiedPlayer);
        }
    }

    @Override
    public void executePlayEffect(UUID player, String entityEffect) {
    }

    @Override
    public void executeTeleport(UUID player, String world, double x, double y, double z, float yaw, float pitch) {
    }

    @Override
    public void executeConnect(UUID player, String server) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);
        if (proxiedPlayer != null && serverInfo != null) {
            proxiedPlayer.connect(serverInfo);
        }
    }
}
