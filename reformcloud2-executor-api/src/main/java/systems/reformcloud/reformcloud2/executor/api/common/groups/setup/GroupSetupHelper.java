package systems.reformcloud.reformcloud2.executor.api.common.groups.setup;

import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.basic.DefaultProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.setup.basic.BasicGroupSetupVersion;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Version;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public final class GroupSetupHelper {

    private GroupSetupHelper() {
        throw new UnsupportedOperationException();
    }

    private static final Collection<GroupSetupVersion> AVAILABLE = Arrays.asList(
            new BasicGroupSetupVersion(
                    "java-bungee-lobby-1.8.8",
                    new ProcessGroup[] {
                            new DefaultProcessGroup("Lobby", 41000, Version.PAPER_1_8_8,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.WATERFALL,
                                    256, true, 512)
                    }, new MainGroup[] {
                            new MainGroup("Proxies", Collections.singletonList("Proxy")),
                            new MainGroup("Lobbies", Collections.singletonList("Lobby"))
                    }
            ), new BasicGroupSetupVersion(
                    "java-bungee-lobby-1.12.2",
                    new ProcessGroup[] {
                            new DefaultProcessGroup("Lobby", 41000, Version.PAPER_1_12_2,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.WATERFALL,
                                    256, true, 512)
                    }, new MainGroup[] {
                            new MainGroup("Proxies", Collections.singletonList("Proxy")),
                            new MainGroup("Lobbies", Collections.singletonList("Lobby"))
                    }
            ), new BasicGroupSetupVersion(
                    "java-bungee-lobby-1.13.2",
                    new ProcessGroup[] {
                            new DefaultProcessGroup("Lobby", 41000, Version.PAPER_1_13_2,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.WATERFALL,
                                    256, true, 512)
                    }, new MainGroup[] {
                            new MainGroup("Proxies", Collections.singletonList("Proxy")),
                            new MainGroup("Lobbies", Collections.singletonList("Lobby"))
                    }
            ), new BasicGroupSetupVersion(
                    "java-bungee-lobby-1.14.4",
                    new ProcessGroup[] {
                            new DefaultProcessGroup("Lobby", 41000, Version.PAPER_1_14_4,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.WATERFALL,
                                    256, true, 512)
                    }, new MainGroup[] {
                            new MainGroup("Proxies", Collections.singletonList("Proxy")),
                            new MainGroup("Lobbies", Collections.singletonList("Lobby"))
                    }
            ), new BasicGroupSetupVersion(
                    "java-bungee-lobby-1.15.2",
                    new ProcessGroup[] {
                            new DefaultProcessGroup("Lobby", 41000, Version.PAPER_1_15_2,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.WATERFALL,
                                    256, true, 512)
                    }, new MainGroup[] {
                            new MainGroup("Proxies", Collections.singletonList("Proxy")),
                            new MainGroup("Lobbies", Collections.singletonList("Lobby"))
                    }
            ), new BasicGroupSetupVersion(
                    "java-default",
                    new ProcessGroup[] {
                            new DefaultProcessGroup("Lobby", 41000, Version.PAPER_1_15_2,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.WATERFALL,
                                    256, true, 512)
                    }, new MainGroup[] {
                            new MainGroup("Proxies", Collections.singletonList("Proxy")),
                            new MainGroup("Lobbies", Collections.singletonList("Lobby"))
                    }
            ), new BasicGroupSetupVersion(
                    "java-proxy-glowstone-1.12.2",
                    new ProcessGroup[] {
                            new DefaultProcessGroup("Lobby", 41000, Version.GLOWSTONE_1_12_2,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.WATERFALL,
                                    256, true, 512)
                    }, new MainGroup[] {
                            new MainGroup("Proxies", Collections.singletonList("Proxy")),
                            new MainGroup("Lobbies", Collections.singletonList("Lobby"))
                    }
            ), new BasicGroupSetupVersion(
                    "java-proxy-akarin-1.12.2",
                    new ProcessGroup[] {
                            new DefaultProcessGroup("Lobby", 41000, Version.AKARIN_1_12_2,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.WATERFALL,
                                    256, true, 512)
                    }, new MainGroup[] {
                            new MainGroup("Proxies", Collections.singletonList("Proxy")),
                            new MainGroup("Lobbies", Collections.singletonList("Lobby"))
                    }
            ), new BasicGroupSetupVersion(
                    "java-proxy-spongevanilla-1.12.2",
                    new ProcessGroup[] {
                            new DefaultProcessGroup("Lobby", 41000, Version.SPONGEVANILLA_1_12_2,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.WATERFALL,
                                    256, true, 512)
                    }, new MainGroup[] {
                            new MainGroup("Proxies", Collections.singletonList("Proxy")),
                            new MainGroup("Lobbies", Collections.singletonList("Lobby"))
                    }
            ), new BasicGroupSetupVersion(
                    "java-proxy-spongeforge-1.12.2",
                    new ProcessGroup[] {
                            new DefaultProcessGroup("Lobby", 41000, Version.SPONGEFORGE_1_12_2,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 25565, Version.WATERFALL,
                                    256, true, 512)
                    }, new MainGroup[] {
                            new MainGroup("Proxies", Collections.singletonList("Proxy")),
                            new MainGroup("Lobbies", Collections.singletonList("Lobby"))
                    }
            ), new BasicGroupSetupVersion(
                    "pe-lobby-proxy",
                    new ProcessGroup[] {
                            new DefaultProcessGroup("Lobby", 41000, Version.NUKKIT_X,
                                    512, false, 50, false, true),
                            new DefaultProcessGroup("Proxy", 19132, Version.WATERDOG_PE,
                                    256, true, 512)
                    }, new MainGroup[] {
                            new MainGroup("Proxies", Collections.singletonList("Proxy")),
                            new MainGroup("Lobbies", Collections.singletonList("Lobby"))
                    }
            ), new BasicGroupSetupVersion("nothing", new ProcessGroup[0], new MainGroup[0])
    );

    public static void printAvailable() {
        AVAILABLE.forEach(e -> System.out.println(e.getName()));
    }

    @Nullable
    public static GroupSetupVersion findByName(@Nonnull String name) {
        return AVAILABLE.stream().filter(e -> e.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
