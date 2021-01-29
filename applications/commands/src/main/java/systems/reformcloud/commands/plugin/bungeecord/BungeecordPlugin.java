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
package systems.reformcloud.commands.plugin.bungeecord;

import net.md_5.bungee.api.plugin.Plugin;
import systems.refomcloud.embedded.Embedded;
import systems.reformcloud.commands.application.packet.PacketGetCommandsConfigResult;
import systems.reformcloud.commands.plugin.CommandConfigHandler;
import systems.reformcloud.commands.plugin.bungeecord.handler.BungeeCommandConfigHandler;
import systems.reformcloud.commands.plugin.packet.PacketReleaseCommandsConfig;
import systems.reformcloud.commands.application.packet.PacketGetCommandsConfig;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.network.PacketIds;
import systems.reformcloud.network.packet.PacketProvider;

public class BungeecordPlugin extends Plugin {

  private static BungeecordPlugin instance;

  public static BungeecordPlugin getInstance() {
    return instance;
  }

  @Override
  public void onEnable() {
    instance = this;
    CommandConfigHandler.setInstance(new BungeeCommandConfigHandler());

    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(PacketProvider.class).registerPacket(PacketGetCommandsConfigResult.class);
    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(PacketProvider.class).registerPacket(PacketReleaseCommandsConfig.class);

    Embedded.getInstance().sendSyncQuery(new PacketGetCommandsConfig()).ifPresent(e -> {
      if (e instanceof PacketGetCommandsConfigResult) {
        CommandConfigHandler.getInstance().handleCommandConfigRelease(((PacketGetCommandsConfigResult) e).getCommandsConfig());
      }
    });
  }

  @Override
  public void onDisable() {
    CommandConfigHandler.getInstance().unregisterAllCommands();
    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(PacketProvider.class).unregisterPacket(PacketIds.RESERVED_EXTRA_BUS + 3);
    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(PacketProvider.class).unregisterPacket(PacketIds.RESERVED_EXTRA_BUS + 2);
  }
}
