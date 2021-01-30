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
package systems.reformcloud.commands.application;

import org.jetbrains.annotations.Nullable;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.application.Application;
import systems.reformcloud.application.updater.ApplicationUpdateRepository;
import systems.reformcloud.commands.application.listener.ProcessInclusionHandler;
import systems.reformcloud.commands.application.packet.PacketGetCommandsConfig;
import systems.reformcloud.commands.application.update.CommandAddonUpdater;
import systems.reformcloud.commands.config.CommandsConfig;
import systems.reformcloud.commands.plugin.packet.PacketReleaseCommandsConfig;
import systems.reformcloud.configuration.JsonConfiguration;
import systems.reformcloud.event.EventManager;
import systems.reformcloud.network.PacketIds;
import systems.reformcloud.network.channel.NetworkChannel;
import systems.reformcloud.network.channel.manager.ChannelManager;
import systems.reformcloud.network.packet.PacketProvider;
import systems.reformcloud.shared.io.IOUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class ReformCloudApplication extends Application {

  private static final ApplicationUpdateRepository REPOSITORY = new CommandAddonUpdater();
  private static ReformCloudApplication instance;
  private static CommandsConfig commandsConfig;

  public static CommandsConfig getCommandsConfig() {
    return commandsConfig;
  }

  public static ReformCloudApplication getInstance() {
    return instance;
  }

  @Override
  public void onLoad() {
    instance = this;
  }

  @Override
  public void onEnable() {
    final Path path = this.getDataDirectory().resolve("config.json");
    if (Files.notExists(path)) {
      IOUtils.createDirectory(path.getParent());
      JsonConfiguration.newJsonConfiguration()
        .add("config", new CommandsConfig(
          true, Arrays.asList("l", "leave", "lobby", "hub", "quit"),
          true, Arrays.asList("reformcloud", "rc", "cloud")
        )).write(path);
    }

    commandsConfig = JsonConfiguration.newJsonConfiguration(path).get("config", CommandsConfig.class);

    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(PacketProvider.class).registerPacket(PacketGetCommandsConfig.class);
    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).registerListener(new ProcessInclusionHandler());

    for (NetworkChannel registeredChannel : ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class).getRegisteredChannels()) {
      if (registeredChannel.isKnown()) {
        registeredChannel.sendPacket(new PacketReleaseCommandsConfig(commandsConfig));
      }
    }
  }

  @Override
  public void onDisable() {
    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(PacketProvider.class).unregisterPacket(PacketIds.RESERVED_EXTRA_BUS + 1);
  }

  @Nullable
  @Override
  public ApplicationUpdateRepository getUpdateRepository() {
    return REPOSITORY;
  }
}
