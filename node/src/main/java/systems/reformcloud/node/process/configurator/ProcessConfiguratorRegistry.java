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
package systems.reformcloud.node.process.configurator;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.node.process.configurator.defaults.bungee.BungeeConfigurator;
import systems.reformcloud.node.process.configurator.defaults.cloudburst.CloudBurstConfigurator;
import systems.reformcloud.node.process.configurator.defaults.cloudburst.NukkitConfigurator;
import systems.reformcloud.node.process.configurator.defaults.glowstone.GlowStoneConfigurator;
import systems.reformcloud.node.process.configurator.defaults.spigot.SpigotConfigurator;
import systems.reformcloud.node.process.configurator.defaults.sponge.SpongeForgeConfigurator;
import systems.reformcloud.node.process.configurator.defaults.sponge.SpongeVanillaConfigurator;
import systems.reformcloud.node.process.configurator.defaults.velocity.VelocityConfigurator;
import systems.reformcloud.node.process.configurator.defaults.waterdog.WaterdogConfigurator;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ProcessConfiguratorRegistry {

  private static final Map<String, ProcessConfigurator> CONFIGURATORS = new ConcurrentHashMap<>();

  private ProcessConfiguratorRegistry() {
    throw new UnsupportedOperationException();
  }

  @NotNull
  public static Optional<ProcessConfigurator> getConfigurator(@NotNull String name) {
    return Optional.ofNullable(CONFIGURATORS.get(name.toLowerCase(Locale.ROOT)));
  }

  public static void registerConfigurator(@NotNull ProcessConfigurator configurator) {
    CONFIGURATORS.putIfAbsent(configurator.getName().toLowerCase(Locale.ROOT), configurator);
  }

  public static void unregisterConfigurator(@NotNull String name) {
    CONFIGURATORS.remove(name.toLowerCase(Locale.ROOT));
  }

  public static void registerDefaults() {
    // proxies
    registerConfigurator(new BungeeConfigurator());
    registerConfigurator(new WaterdogConfigurator());
    registerConfigurator(new VelocityConfigurator());
    // mc pe
    registerConfigurator(new CloudBurstConfigurator());
    registerConfigurator(new NukkitConfigurator());
    // mc java
    registerConfigurator(new GlowStoneConfigurator());
    registerConfigurator(new SpigotConfigurator());
    registerConfigurator(new SpongeVanillaConfigurator());
    registerConfigurator(new SpongeForgeConfigurator());
  }
}
