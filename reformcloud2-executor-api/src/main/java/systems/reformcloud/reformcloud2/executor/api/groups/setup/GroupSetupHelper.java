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
package systems.reformcloud.reformcloud2.executor.api.groups.setup;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.basic.DefaultProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.setup.basic.BasicGroupSetupVersion;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Version;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public final class GroupSetupHelper {

    private static final Collection<GroupSetupVersion> AVAILABLE = Arrays.asList(
            new BasicGroupSetupVersion(
                    "java-bungee-lobby-1.8.8",
                    new ProcessGroup[]{
                            new DefaultProcessGroup("Lobby", 41000, Version.PAPER_1_8_8,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.WATERFALL,
                                    256, true, 512)
                    }, new MainGroup[]{
                    new MainGroup("Proxies", Collections.singletonList("Proxy")),
                    new MainGroup("Lobbies", Collections.singletonList("Lobby"))
            }
            ), new BasicGroupSetupVersion(
                    "java-velocity-lobby-1.8.8",
                    new ProcessGroup[]{
                            new DefaultProcessGroup("Lobby", 41000, Version.PAPER_1_8_8,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.VELOCITY,
                                    256, true, 512)
                    }, new MainGroup[]{
                    new MainGroup("Proxies", Collections.singletonList("Proxy")),
                    new MainGroup("Lobbies", Collections.singletonList("Lobby"))
            }
            ),
            new BasicGroupSetupVersion(
                    "java-bungee-lobby-1.12.2",
                    new ProcessGroup[]{
                            new DefaultProcessGroup("Lobby", 41000, Version.PAPER_1_12_2,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.WATERFALL,
                                    256, true, 512)
                    }, new MainGroup[]{
                    new MainGroup("Proxies", Collections.singletonList("Proxy")),
                    new MainGroup("Lobbies", Collections.singletonList("Lobby"))
            }
            ), new BasicGroupSetupVersion(
                    "java-bungee-lobby-1.13.2",
                    new ProcessGroup[]{
                            new DefaultProcessGroup("Lobby", 41000, Version.PAPER_1_13_2,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.WATERFALL,
                                    256, true, 512)
                    }, new MainGroup[]{
                    new MainGroup("Proxies", Collections.singletonList("Proxy")),
                    new MainGroup("Lobbies", Collections.singletonList("Lobby"))
            }
            ), new BasicGroupSetupVersion(
                    "java-velocity-lobby-1.14.4",
                    new ProcessGroup[]{
                            new DefaultProcessGroup("Lobby", 41000, Version.PAPER_1_14_4,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.VELOCITY,
                                    256, true, 512)
                    }, new MainGroup[]{
                    new MainGroup("Proxies", Collections.singletonList("Proxy")),
                    new MainGroup("Lobbies", Collections.singletonList("Lobby"))
            }
            ), new BasicGroupSetupVersion(
                    "java-bungee-lobby-1.14.4",
                    new ProcessGroup[]{
                            new DefaultProcessGroup("Lobby", 41000, Version.PAPER_1_14_4,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.WATERFALL,
                                    256, true, 512)
                    }, new MainGroup[]{
                    new MainGroup("Proxies", Collections.singletonList("Proxy")),
                    new MainGroup("Lobbies", Collections.singletonList("Lobby"))
            }
            ), new BasicGroupSetupVersion(
                    "java-velocity-lobby-1.16.3",
                    new ProcessGroup[]{
                            new DefaultProcessGroup("Lobby", 41000, Version.PAPER_1_16_3,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.VELOCITY,
                                    256, true, 512)
                    }, new MainGroup[]{
                    new MainGroup("Proxies", Collections.singletonList("Proxy")),
                    new MainGroup("Lobbies", Collections.singletonList("Lobby"))
            }
            ), new BasicGroupSetupVersion(
                    "java-bungee-lobby-1.16.3",
                    new ProcessGroup[]{
                            new DefaultProcessGroup("Lobby", 41000, Version.PAPER_1_16_3,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.WATERFALL,
                                    256, true, 512)
                    }, new MainGroup[]{
                    new MainGroup("Proxies", Collections.singletonList("Proxy")),
                    new MainGroup("Lobbies", Collections.singletonList("Lobby"))
            }
            ), new BasicGroupSetupVersion(
                    "java-proxy-glowstone-1.12.2",
                    new ProcessGroup[]{
                            new DefaultProcessGroup("Lobby", 41000, Version.GLOWSTONE_1_12_2,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.WATERFALL,
                                    256, true, 512)
                    }, new MainGroup[]{
                    new MainGroup("Proxies", Collections.singletonList("Proxy")),
                    new MainGroup("Lobbies", Collections.singletonList("Lobby"))
            }
            ), new BasicGroupSetupVersion(
                    "java-proxy-akarin-1.12.2",
                    new ProcessGroup[]{
                            new DefaultProcessGroup("Lobby", 41000, Version.AKARIN_1_12_2,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.WATERFALL,
                                    256, true, 512)
                    }, new MainGroup[]{
                    new MainGroup("Proxies", Collections.singletonList("Proxy")),
                    new MainGroup("Lobbies", Collections.singletonList("Lobby"))
            }
            ), new BasicGroupSetupVersion(
                    "java-proxy-spongevanilla-1.12.2",
                    new ProcessGroup[]{
                            new DefaultProcessGroup("Lobby", 41000, Version.SPONGEVANILLA_1_12_2,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.WATERFALL,
                                    256, true, 512)
                    }, new MainGroup[]{
                    new MainGroup("Proxies", Collections.singletonList("Proxy")),
                    new MainGroup("Lobbies", Collections.singletonList("Lobby"))
            }
            ), new BasicGroupSetupVersion(
                    "java-proxy-spongeforge-1.12.2",
                    new ProcessGroup[]{
                            new DefaultProcessGroup("Lobby", 41000, Version.SPONGEFORGE_1_12_2,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.WATERFALL,
                                    256, true, 512)
                    }, new MainGroup[]{
                    new MainGroup("Proxies", Collections.singletonList("Proxy")),
                    new MainGroup("Lobbies", Collections.singletonList("Lobby"))
            }
            ), new BasicGroupSetupVersion(
                    "java-default",
                    new ProcessGroup[]{
                            new DefaultProcessGroup("Lobby", 41000, Version.PAPER_1_16_3,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.WATERFALL,
                                    256, true, 512)
                    }, new MainGroup[]{
                    new MainGroup("Proxies", Collections.singletonList("Proxy")),
                    new MainGroup("Lobbies", Collections.singletonList("Lobby"))
            }
            ), new BasicGroupSetupVersion(
                    "pe-lobby-proxy",
                    new ProcessGroup[]{
                            new DefaultProcessGroup("Lobby", 41000, Version.NUKKIT_X,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 19132, Version.WATERDOG_PE,
                                    256, true, 512)
                    }, new MainGroup[]{
                    new MainGroup("Proxies", Collections.singletonList("Proxy")),
                    new MainGroup("Lobbies", Collections.singletonList("Lobby"))
            }
            ), new BasicGroupSetupVersion("nothing", new ProcessGroup[0], new MainGroup[0])
    );

    private GroupSetupHelper() {
        throw new UnsupportedOperationException();
    }

    public static void printAvailable() {
        AVAILABLE.forEach(e -> System.out.println(e.getName()));
    }

    @Nullable
    public static GroupSetupVersion findByName(@NotNull String name) {
        return AVAILABLE.stream().filter(e -> e.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
