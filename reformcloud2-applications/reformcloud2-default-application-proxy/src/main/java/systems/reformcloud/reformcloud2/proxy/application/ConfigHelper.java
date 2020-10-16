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
package systems.reformcloud.reformcloud2.proxy.application;

import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.io.IOUtils;
import systems.reformcloud.reformcloud2.proxy.ProxyConfiguration;
import systems.reformcloud.reformcloud2.proxy.config.MotdConfiguration;
import systems.reformcloud.reformcloud2.proxy.config.TabListConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public final class ConfigHelper {

    private static ProxyConfiguration proxyConfiguration;

    private ConfigHelper() {
        throw new UnsupportedOperationException();
    }

    public static void init(File app) {
        if (!Files.exists(app.toPath()) || !Files.exists(Paths.get(app.toString(), "config.json"))) {
            IOUtils.createDirectory(app.toPath());
            new JsonConfiguration()
                .add("config", new ProxyConfiguration(
                    Arrays.asList(
                        new MotdConfiguration(
                            "§b§lReform§f§lCloud §8» §7Server Network §8» [§f§l1.8§7-§6§l1.16§8]",
                            "§b§lN§f§lews §8» §7§lWe are §a§lonline§7§l!",
                            new String[]{
                                " ",
                                "§8× §7powered by §bReform§fCloud",
                                "§8§m-------------------------",
                                "§8× §7Discord: §bhttps://discord.gg/uskXdVZ",
                                " "
                            },
                            "§a§l✔ §8● §6%proxy_online_players%§8/§f%proxy_max_players%",
                            2
                        ), new MotdConfiguration(
                            "§b§lReform§f§lCloud §8» §7Check out §b§lSpigot§f§lMC",
                            "§b§lN§f§lews §8» §7§lWe are §a§lonline§7§l!",
                            new String[]{
                                " ",
                                "§8× §7powered by §bReform§fCloud",
                                "§8§m-------------------------",
                                "§8× §7Discord: §bhttps://discord.gg/uskXdVZ",
                                " "
                            },
                            "§a§l✔ §8● §6%proxy_online_players%§8/§f%proxy_max_players%",
                            2
                        )
                    ), Arrays.asList(
                    new MotdConfiguration(
                        "§b§lReform§f§lCloud §8» §7Server Network §8» [§f§l1.8§7-§6§l1.16§8]",
                        "§b§lN§f§lews §8» §7§lWe are in §c§lmaintenance§7§l!",
                        new String[]{
                            " ",
                            "§8× §7powered by §bReform§fCloud",
                            "§8§m-------------------------",
                            "§8× §7Discord: §bhttps://discord.gg/uskXdVZ",
                            " "
                        },
                        "§c§l✘ §8● §4§lMAINTENANCE",
                        2
                    ), new MotdConfiguration(
                        "§b§lReform§f§lCloud §8» §7Check out §b§lSpigot§f§lMC",
                        "§b§lN§f§lews §8» §7§lWe are in §c§lmaintenance§7§l!",
                        new String[]{
                            " ",
                            "§8× §7powered by §bReform§fCloud",
                            "§8§m-------------------------",
                            "§8× §7Discord: §bhttps://discord.gg/uskXdVZ",
                            " "
                        },
                        "§c§l✘ §8● §4§lMAINTENANCE",
                        2
                    )
                ), Arrays.asList(
                    new TabListConfiguration(
                        "\n §8§l» §b§lReform§f§lCloud §8§l➥ §7%proxy_online_players%§8/§7%proxy_max_players% §8§l« \n §7Server §8§l➟ §6%player_server% \n",
                        "\n §7Discord §8§l➟ §bhttps://discord.gg/uskXdVZ \n §7Twitter §8§l➟ §7@§bReform§fCloud \n",
                        2
                    ), new TabListConfiguration(
                        "\n §8§l» §b§lReform§f§lCloud §8§l➥ §7%proxy_online_players%§8/§7%proxy_max_players% §8§l« \n §8§l➟ §7§lNot just a cloud system, but an experience \n",
                        "\n §7Discord §8§l➟ §bhttps://discord.gg/uskXdVZ \n §7Twitter §8§l➟ §7@§bReform§fCloud \n",
                        2
                    )
                )
                )).write(Paths.get(app.toString(), "config.json"));
        }

        proxyConfiguration = JsonConfiguration.read(Paths.get(app.toString(), "config.json"))
            .get("config", ProxyConfiguration.TYPE);
    }

    public static ProxyConfiguration getProxyConfiguration() {
        return proxyConfiguration;
    }
}
