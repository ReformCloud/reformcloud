package de.klaro.reformcloud2.executor.api.common.groups.utils;

import de.klaro.reformcloud2.executor.api.common.utility.system.DownloadHelper;

import java.util.Arrays;
import java.util.Deque;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public enum Version {

    /**
     * Spigot Versions
     */
    SPIGOT_1_7_10("Spigot 1.7.10", "1.7.10",
            "https://archive.mcmirror.io/Spigot/spigot-1.7.10-SNAPSHOT-b1643.jar", 1),
    SPIGOT_1_8("Spigot 1.8", "1.8",
            "https://archive.mcmirror.io/Spigot/spigot-1.8-R0.1-SNAPSHOT.jar", 1),
    SPIGOT_1_8_3("Spigot 1.8.3", "1.8.3",
            "https://archive.mcmirror.io/Spigot/spigot-1.8.3-R0.1-SNAPSHOT-latest.jar", 1),
    SPIGOT_1_8_4("Spigot 1.8.4", "1.8.4",
            "https://archive.mcmirror.io/Spigot/spigot-1.8.4-R0.1-SNAPSHOT-latest.jar", 1),
    SPIGOT_1_8_5("Spigot 1.8.5", "1.8.5",
            "https://archive.mcmirror.io/Spigot/spigot-1.8.5-R0.1-SNAPSHOT-latest.jar", 1),
    SPIGOT_1_8_6("Spigot 1.8.6", "1.8.6",
            "https://archive.mcmirror.io/Spigot/spigot-1.8.6-R0.1-SNAPSHOT-latest.jar", 1),
    SPIGOT_1_8_7("Spigot 1.8.7", "1.8.7",
            "https://archive.mcmirror.io/Spigot/spigot-1.8.7-R0.1-SNAPSHOT-latest.jar", 1),
    SPIGOT_1_8_8("Spigot 1.8.8", "1.8.8",
            "https://archive.mcmirror.io/Spigot/spigot-1.8.8-R0.1-SNAPSHOT-latest.jar", 1),
    SPIGOT_1_9("Spigot 1.9-R0.1-SNAPSHOT", "1.9",
            "https://archive.mcmirror.io/Spigot/spigot-api-1.9-R0.1-SNAPSHOT-latest.jar", 1),
    SPIGOT_1_9_2("Spigot 1.9.2", "1.9.2",
            "https://archive.mcmirror.io/Spigot/spigot-1.9.2-R0.1-SNAPSHOT-latest.jar", 1),
    SPIGOT_1_9_4("Spigot 1.9.4", "1.9.4",
            "https://archive.mcmirror.io/Spigot/spigot-1.9.4-R0.1-SNAPSHOT-latest.jar", 1),
    SPIGOT_1_10("Spigot 1.10-R0.1-SNAPSHOT", "1.10",
            "https://archive.mcmirror.io/Spigot/spigot-api-1.10-R0.1-SNAPSHOT-latest.jar", 1),
    SPIGOT_1_10_2("Spigot 1.10.2", "1.10.2",
            "https://archive.mcmirror.io/Spigot/spigot-1.10.2-R0.1-SNAPSHOT-latest.jar", 1),
    SPIGOT_1_11("Spigot 1.11-R0-SNAPSHOT", "1.11",
            "https://archive.mcmirror.io/Spigot/spigot-api-1.11-R0.1-SNAPSHOT-latest.jar", 1),
    SPIGOT_1_11_2("Spigot 1.11.2-R0-SNAPSHOT", "1.11.2",
            "https://archive.mcmirror.io/Spigot/spigot-api-1.11.2-R0.1-SNAPSHOT.jar", 1),
    SPIGOT_1_12("Spigot 1.12", "1.12",
            "https://mcmirror.io/files/Spigot/Spigot-1.12-596221b_d00c057-20170726-0522.jar", 1),
    SPIGOT_1_12_1("Spigot 1.12.1", "1.12.1",
            "https://mcmirror.io/files/Spigot/Spigot-1.12.1-da42974_8f47214-20170909-0744.jar", 1),
    SPIGOT_1_12_2("Spigot 1.12.2", "1.12.2",
            "https://mcmirror.io/files/Spigot/Spigot-1.12.2-e8ded36-20181110-0947.jar", 1),
    SPIGOT_1_13("Spigot 1.13", "1.13",
            "https://mcmirror.io/files/Spigot/Spigot-1.13-fe3ab0d_1bc2433-20180815-2348.jar", 1),
    SPIGOT_1_13_1("Spigot 1.13.1", "1.13.1",
            "https://mcmirror.io/files/Spigot/Spigot-1.13.1-f6a273b_1ceee63-20180926-0919.jar", 1),
    SPIGOT_1_13_2("Spigot 1.13.2", "1.13.2",
            "https://mcmirror.io/files/Spigot/Spigot-1.13.2-0c02b0c-20190425-0538.jar", 1),
    SPIGOT_1_14("Spigot 1.14", "1.14",
            "https://mcmirror.io/files/Spigot/Spigot-1.14-8043ebc-20190514-0000.jar", 1),
    SPIGOT_1_14_1("Spigot 1.14.1", "1.14.1",
            "https://mcmirror.io/files/Spigot/Spigot-1.14.1-03bd4b0-20190520-1053.jar", 1),
    SPIGOT_1_14_2("Spigot 1.14.2", "1.14.2",
            "https://mcmirror.io/files/Spigot/Spigot-1.14.2-baafee9-20190602-0956.jar", 1),
    SPIGOT_1_14_3("Spigot 1.14.3", "1.14.3",
            "https://mcmirror.io/files/Spigot/Spigot-1.14.3-d05d3c1-20190703-0030.jar", 1),
    SPIGOT_1_14_4("Spigot 1.14.4", "1.14.4",
            "https://mcmirror.io/files/Spigot/Spigot-1.14.4-9de398a-20190719-2300.jar", 1),

    /**
     * Paper Versions
     */
    PAPER_1_7_10("Paper 1.7.10", "1.7.10",
            "https://archive.mcmirror.io/Paper/Paper-1.7.10-R0.1-SNAPSHOT-latest.jar", 1),
    PAPER_1_8("Paper 1.8-R0-1-SNAPSHOT", "1.8",
            "https://archive.mcmirror.io/Paper/Paper-1.8-R0.1-SNAPSHOT-b235.jar", 1),
    PAPER_1_8_3("Paper 1.8.3-R0-1-SNAPSHOT", "1.8.3",
            "https://archive.mcmirror.io/Paper/Paper-1.8.3-R0.1-SNAPSHOT-b253.jar", 1),
    PAPER_1_8_4("Paper 1.8.4-R0-1-SNAPSHOT", "1.8.4",
            "https://archive.mcmirror.io/Paper/Paper-1.8.4-R0.1-SNAPSHOT-latest.jar", 1),
    PAPER_1_8_5("Paper 1.8.5-R0-1-SNAPSHOT", "1.8.5",
            "https://archive.mcmirror.io/Paper/Paper-1.8.5-R0.1-SNAPSHOT-latest.jar", 1),
    PAPER_1_8_6("Paper 1.8.6-R0-1-SNAPSHOT", "1.8.6",
            "https://archive.mcmirror.io/Paper/Paper-1.8.6-R0.1-SNAPSHOT-latest.jar", 1),
    PAPER_1_8_7("Paper 1.8.7-R0-1-SNAPSHOT", "1.8.7",
            "https://archive.mcmirror.io/Paper/Paper-1.8.7-R0.1-SNAPSHOT-latest.jar", 1),
    PAPER_1_8_8("Paper 1.8.8-R0-1-SNAPSHOT", "1.8.8",
            "https://archive.mcmirror.io/Paper/Paper-1.8.8-R0.1-SNAPSHOT-latest.jar", 1),
    PAPER_1_11_2("Paper 1.11.2", "1.11.2",
            "https://archive.mcmirror.io/Paper/Paper-1.11.2-b1000.jar", 1),
    PAPER_1_12_2("Paper 1.12.2", "1.12.2",
            "https://mcmirror.io/files/Paper/Paper-1.12.2-ac69748-20181207-0309.jar", 1),
    PAPER_1_13_2("Paper 1.13.2", "1.13.2",
            "https://mcmirror.io/files/Paper/Paper-1.13.2-fb25dc1-20190422-2136.jar", 1),
    PAPER_1_14_1("Paper 1.14.1", "1.14.1",
            "https://yivesmirror.com/files/paper/Paper-1.14.1-b42.jar", 1),
    PAPER_1_14_2("Paper 1.14.2", "1.14.2",
            "https://mcmirror.io/files/Paper/Paper-1.14.2-bf1d217-20190624-0232.jar", 1),
    PAPER_1_14_3("Paper 1.14.3", "1.14.3",
            "https://mcmirror.io/files/Paper/Paper-1.14.3-1bacdbd-20190702-1850.jar", 1),
    PAPER_1_14_4("Paper 1.14.4", "1.14.4",
            "https://mcmirror.io/files/Paper/Paper-1.14.4-9fe63a1-20190720-0401.jar", 1),

    /**
     * SpongeVanilla Versions
     */
    SPONGEVANILLA_1_8_9("SpongeVanilla 1.8.9", "1.8.9",
            "https://archive.mcmirror.io/SpongeVanilla/spongevanilla-1.8.9-4.2.0-BETA-352.jar", 1),
    SPONGEVANILLA_1_9_4("SpongeVanilla 1.9.4", "1.9.4",
            "https://repo.spongepowered.org/maven/org/spongepowered/spongevanilla/1.9.4-5.0.0-BETA-83/spongevanilla-1.9.4-5.0.0-BETA-83.jar", 1),
    SPONGEVANILLA_1_10_2("SpongeVanilla 1.10.2", "1.10.2",
            "https://archive.mcmirror.io/SpongeVanilla/spongevanilla-1.10.2-5.2.0-BETA-403.jar", 1),
    SPONGEVANILLA_1_11_2("SpongeVanilla 1.11.2", "1.11.2",
            "https://archive.mcmirror.io/SpongeVanilla/spongevanilla-1.11.2-6.1.0-BETA-27.jar", 1),
    SPONGEVANILLA_1_12_2("SpongeVanilla 1.12.2", "1.12.2",
            "https://archive.mcmirror.io/SpongeVanilla/spongevanilla-1.12.2-7.1.0-BETA-59.jar", 1),
    /**
     * SpongeForge Versions
     */
    SPONGEFORGE_1_8_9("SpongeForge 1.8.9", "1.8.9",
            "https://dl.reformcloud.systems/forge/sponge-1.8.9.zip", 1),
    SPONGEFORGE_1_10_2("SpongeForge 1.10.2", "1.10.2",
            "https://dl.reformcloud.systems/forge/sponge-1.10.2.zip", 1),
    SPONGEFORGE_1_11_2("SpongeForge 1.11.2", "1.11.2",
            "https://dl.reformcloud.systems/forge/sponge-1.11.2.zip", 1),
    SPONGEFORGE_1_12_2("SpongeForge 1.12.2", "1.12.2",
            "https://dl.reformcloud.systems/forge/sponge-1.12.2.zip", 1),
    /**
     * TacoSpigot Versions
     */
    TACO_1_8_8("Taco 1.8.8", "1.8.8",
            "https://mcmirror.io/files/TacoSpigot/TacoSpigot-1.8.8-95870a9-20180608-0352.jar", 1),
    TACO_1_11_2("Taco 1.11.2", "1.11.2",
            "https://mcmirror.io/files/TacoSpigot/TacoSpigot-1.11.2-8aa5e7e-20170515-0636.jar", 1),
    TACO_1_12_2("Taco 1.12.2", "1.12.2",
            "https://mcmirror.io/files/TacoSpigot/TacoSpigot-1.12.2-f8ba67d-20180610-1914.jar", 1),
    /**
     * TorchSpigot Versions
     */
    TORCH_1_8_8("Torch 1.8.8", "1.8.8",
            "https://archive.mcmirror.io/Torch/Torch-1.8.8-R0.1.3-RC4.jar", 1),
    TORCH_1_9_4("Torch 1.9.4", "1.9.4",
            "https://archive.mcmirror.io/Torch/Torch-1.9.4-R2.0-Light-RELEASE.jar", 1),
    TORCH_1_11_2("Torch 1.11.2", "1.11.2",
            "https://archive.mcmirror.io/Torch/Torchpowered-latest.jar", 1),
    /**
     * Hose Versions
     */
    HOSE_1_8_8("Hose 1.8.8", "1.8.8",
            "https://archive.mcmirror.io/HOSE/hose-1.8.8.jar", 1),
    HOSE_1_9_4("Hose 1.9.4", "1.9.4",
            "https://archive.mcmirror.io/HOSE/hose-1.9.4.jar", 1),
    HOSE_1_10_2("Hose 1.10.2", "1.10.2",
            "https://archive.mcmirror.io/HOSE/hose-1.10.2.jar", 1),
    /**
     * Akarin Versions
     */
    AKARIN_1_12_2("Akarin 1.12.2", "1.12.2",
            "https://github.com/Akarin-project/Akarin/releases/download/1.12.2-R0.4.2/akarin-1.12.2.jar", 1),
    /**
     * GlowStone Versions
     */
    GLOWSTONE_1_7_9("Glowstone 1.7.9", "1.7.9",
            "https://archive.mcmirror.io/GlowStone/glowstone-1.7.9-SNAPSHOT.jar", 1),
    GLOWSTONE_1_8_9("Glowstone 1.8.9", "1.8.9",
            "https://archive.mcmirror.io/GlowStone/glowstone-1.8.9-SNAPSHOT.jar", 1),
    GLOWSTONE_1_9_4("Glowstone 1.9.4", "1.9.4",
            "https://archive.mcmirror.io/GlowStone/glowstone-1.9.4-SNAPSHOT.jar", 1),
    GLOWSTONE_1_10_2("Glowstone 1.10.2", "1.10.2",
            "https://archive.mcmirror.io/GlowStone/glowstone-1.10.2-SNAPSHOT.jar", 1),
    GLOWSTONE_1_11_2("Glowstone 1.11.2", "1.11.2",
            "https://archive.mcmirror.io/GlowStone/glowstone-1.11.2-SNAPSHOT.jar", 1),
    GLOWSTONE_1_12_2("Glowstone 1.12.2", "1.12.2",
            "https://github.com/GlowstoneMC/Glowstone/releases/download/2018.0.1/glowstone.jar", 1),
    /**
     * Proxy versions
     */
    BUNGEECORD("BungeeCord", "1.8-1.14",
            "https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar", 2),
    WATERFALL("Waterfall", "1.8-1.14",
            "https://papermc.io/ci/job/Waterfall/lastSuccessfulBuild/artifact/Waterfall-Proxy/bootstrap/target/Waterfall.jar", 2),
    HEXACORD("HexaCord", "1.7-1.14",
            "https://archive.mcmirror.io/HexaCord/HexaCord-v139.jar", 2),
    TRAVERTINE("Travertine", "1.7-1.14",
            "https://archive.mcmirror.io/Travertine/Travertine-latest.jar", 2),
    VELOCITY("Velocity", "1.8-1.14",
            "https://archive.mcmirror.io/Velocity/velocity-proxy-1.0-SNAPSHOT-all.jar", 2),
    /**
     * MCPE server
     */
    NUKKIT_X("NukkitX", "",
            "", 3),
    /**
     * MCPE proxy
     */
    PROX_PROX("ProxProx", "",
            "", 4);

    private static final TreeMap<String, Version> JAVA_SERVER_PROVIDERS = new TreeMap<>();

    private static final TreeMap<String, Version> JAVA_PROXY_PROVIDERS = new TreeMap<>();

    private static final TreeMap<String, Version> POCKET_SERVER_PROVIDERS = new TreeMap<>();

    private static final TreeMap<String, Version> POCKET_PROXY_PROVIDERS = new TreeMap<>();

    public static final Deque<String> AVAILABLE_JAVA_SERVER_VERSIONS = new ConcurrentLinkedDeque<>();

    public static final Deque<String> AVAILABLE_JAVA_PROXY_VERSIONS = new ConcurrentLinkedDeque<>();

    //TODO
    public static final Deque<String> AVAILABLE_POCKET_SERVER_VERSIONS = new ConcurrentLinkedDeque<>();

    public static final Deque<String> AVAILABLE_POCKET_PROXY_VERSIONS = new ConcurrentLinkedDeque<>();

    static {
        AVAILABLE_JAVA_SERVER_VERSIONS.addAll(Arrays.asList(
                "1.7.9",
                "1.7.10",
                "1.8",
                "1.8.3",
                "1.8.4",
                "1.8.5",
                "1.8.6",
                "1.8.7",
                "1.8.8",
                "1.8.9",
                "1.9",
                "1.9.2",
                "1.9.4",
                "1.10",
                "1.10.2",
                "1.11",
                "1.11.2",
                "1.12",
                "1.12.1",
                "1.12.2",
                "1.13",
                "1.13.1",
                "1.13.2",
                "1.14",
                "1.14.1",
                "1.14.2",
                "1.14.3",
                "1.14.4"
        ));

        AVAILABLE_JAVA_PROXY_VERSIONS.addAll(Arrays.asList(
                "1.8-1.14",
                "1.7-1.14"
        ));

        for (Version version : values()) {
            if (version.id == 1 && !JAVA_SERVER_PROVIDERS.containsKey(version.name)) {
                JAVA_SERVER_PROVIDERS.put(version.name, version);
            } else if (version.id == 2 && !JAVA_PROXY_PROVIDERS.containsKey(version.name)) {
                JAVA_PROXY_PROVIDERS.put(version.name, version);
            } else if (version.id == 3 && !POCKET_SERVER_PROVIDERS.containsKey(version.name)) {
                POCKET_SERVER_PROVIDERS.put(version.name, version);
            } else if (version.id == 4 && !POCKET_PROXY_PROVIDERS.containsKey(version.name)) {
                POCKET_PROXY_PROVIDERS.put(version.name, version);
            }
        }
    }

    private final String name;

    private final String version;

    private final String url;

    private final int id;

    Version(final String name, final String version, final String url, int id) {
        this.name = name;
        this.version = version;
        this.url = url;
        this.id = id;
    }

    public static TreeMap<String, Version> getJavaServerProviders() {
        return JAVA_SERVER_PROVIDERS;
    }

    public static TreeMap<String, Version> getJavaProxyProviders() {
        return JAVA_PROXY_PROVIDERS;
    }

    public static TreeMap<String, Version> getPocketServerProviders() {
        return POCKET_SERVER_PROVIDERS;
    }

    public static TreeMap<String, Version> getPocketProxyProviders() {
        return POCKET_PROXY_PROVIDERS;
    }

    public static Deque<String> getAvailableJavaServerVersions() {
        return AVAILABLE_JAVA_SERVER_VERSIONS;
    }

    public static Deque<String> getAvailableJavaProxyVersions() {
        return AVAILABLE_JAVA_PROXY_VERSIONS;
    }

    public static Deque<String> getAvailablePocketServerVersions() {
        return AVAILABLE_POCKET_SERVER_VERSIONS;
    }

    public static Deque<String> getAvailablePocketProxyVersions() {
        return AVAILABLE_POCKET_PROXY_VERSIONS;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getUrl() {
        return url;
    }

    public int getId() {
        return id;
    }

    public static void downloadVersion(Version version) {
        DownloadHelper.downloadAndDisconnect(version.url, "reformcloud/versions/" + version.name + ".jar");
    }
}
