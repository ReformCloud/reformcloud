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
package systems.refomcloud.reformcloud2.embedded.plugin.velocity.executor;

import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.reformcloud2.embedded.executor.PlayerAPIExecutor;
import systems.refomcloud.reformcloud2.embedded.plugin.velocity.VelocityExecutor;

import java.time.Duration;
import java.util.UUID;

public class VelocityPlayerAPIExecutor extends PlayerAPIExecutor {

    private final ProxyServer proxyServer;

    public VelocityPlayerAPIExecutor(@NotNull ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public void executeSendMessage(UUID player, String message) {
        this.proxyServer.getPlayer(player).ifPresent(val -> val.sendMessage(Identity.nil(), VelocityExecutor.SERIALIZER.deserialize(message)));
    }

    @Override
    public void executeKickPlayer(UUID player, String message) {
        this.proxyServer.getPlayer(player).ifPresent(val -> val.disconnect(VelocityExecutor.SERIALIZER.deserialize(message)));
    }

    @Override
    public void executePlaySound(UUID player, String sound, float f1, float f2) {
    }

    @Override
    public void executeSendTitle(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        this.proxyServer.getPlayer(player).ifPresent(val -> {
            Title velocityTitle = Title.title(
                VelocityExecutor.SERIALIZER.deserialize(title),
                VelocityExecutor.SERIALIZER.deserialize(subTitle),
                Title.Times.of(Duration.ofSeconds(fadeIn / 20), Duration.ofSeconds(stay / 20), Duration.ofSeconds(fadeOut / 20))
            );
            val.showTitle(velocityTitle);
        });
    }

    @Override
    public void executePlayEffect(UUID player, String entityEffect) {
    }

    @Override
    public void executeTeleport(UUID player, String world, double x, double y, double z, float yaw, float pitch) {
    }

    @Override
    public void executeConnect(UUID player, String server) {
        this.proxyServer.getPlayer(player).ifPresent(val -> this.proxyServer.getServer(server).ifPresent(s -> val.createConnectionRequest(s).fireAndForget()));
    }
}
