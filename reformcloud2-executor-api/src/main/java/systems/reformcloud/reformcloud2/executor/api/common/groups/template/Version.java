package systems.reformcloud.reformcloud2.executor.api.common.groups.template;

import systems.reformcloud.reformcloud2.executor.api.common.utility.system.DownloadHelper;

import java.util.Arrays;
import java.util.Deque;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public enum Version {

    /**
     * Spigot Versions
     */
    SPIGOT_1_7_10("Spigot 1.7.10", "1.7.10",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.7.10.jar", 1),
    SPIGOT_1_8("Spigot 1.8", "1.8",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.8.jar", 1),
    SPIGOT_1_8_3("Spigot 1.8.3", "1.8.3",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.8.3.jar", 1),
    SPIGOT_1_8_4("Spigot 1.8.4", "1.8.4",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.8.4.jar", 1),
    SPIGOT_1_8_5("Spigot 1.8.5", "1.8.5",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.8.5.jar", 1),
    SPIGOT_1_8_6("Spigot 1.8.6", "1.8.6",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.8.6.jar", 1),
    SPIGOT_1_8_7("Spigot 1.8.7", "1.8.7",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.8.7.jar", 1),
    SPIGOT_1_8_8("Spigot 1.8.8", "1.8.8",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.8.8.jar", 1),
    SPIGOT_1_9("Spigot 1.9-R0.1-SNAPSHOT", "1.9",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.9.jar", 1),
    SPIGOT_1_9_2("Spigot 1.9.2", "1.9.2",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.9.2.jar", 1),
    SPIGOT_1_9_4("Spigot 1.9.4", "1.9.4",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.9.4.jar", 1),
    SPIGOT_1_10("Spigot 1.10-R0.1-SNAPSHOT", "1.10",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.10.jar", 1),
    SPIGOT_1_10_2("Spigot 1.10.2", "1.10.2",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.10.2.jar", 1),
    SPIGOT_1_11("Spigot 1.11-R0-SNAPSHOT", "1.11",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.11.jar", 1),
    SPIGOT_1_11_2("Spigot 1.11.2-R0-SNAPSHOT", "1.11.2",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.11.2.jar", 1),
    SPIGOT_1_12("Spigot 1.12", "1.12",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.12.jar", 1),
    SPIGOT_1_12_1("Spigot 1.12.1", "1.12.1",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.12.1.jar", 1),
    SPIGOT_1_12_2("Spigot 1.12.2", "1.12.2",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.12.2.jar", 1),
    SPIGOT_1_13("Spigot 1.13", "1.13",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.13.jar", 1),
    SPIGOT_1_13_1("Spigot 1.13.1", "1.13.1",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.13.1.jar", 1),
    SPIGOT_1_13_2("Spigot 1.13.2", "1.13.2",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.13.2.jar", 1),
    SPIGOT_1_14("Spigot 1.14", "1.14",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.14.jar", 1),
    SPIGOT_1_14_1("Spigot 1.14.1", "1.14.1",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.14.1.jar", 1),
    SPIGOT_1_14_2("Spigot 1.14.2", "1.14.2",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.14.2.jar", 1),
    SPIGOT_1_14_3("Spigot 1.14.3", "1.14.3",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.14.3.jar", 1),
    SPIGOT_1_14_4("Spigot 1.14.4", "1.14.4",
            "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.14.4.jar", 1),

    /**
     * Paper Versions
     */
    PAPER_1_7_10("Paper 1.7.10", "1.7.10",
            "https://dl.reformcloud.systems/mcversions/paper/paper-1.7.10.jar", 1),
    PAPER_1_8_8("Paper 1.8.8-R0-1-SNAPSHOT", "1.8.8",
            "https://dl.reformcloud.systems/mcversions/paper/paper-1.8.8.jar", 1),
    PAPER_1_9_4("Paper 1.9.4", "1.9.4",
            "https://dl.reformcloud.systems/mcversions/paper/paper-1.9.4.jar", 1),
    PAPER_1_10_2("Paper 1.10.2", "1.10.2",
            "https://dl.reformcloud.systems/mcversions/paper/paper-1.10.2.jar", 1),
    PAPER_1_11_2("Paper 1.11.2", "1.11.2",
            "https://dl.reformcloud.systems/mcversions/paper/paper-1.11.2.jar", 1),
    PAPER_1_12_2("Paper 1.12.2", "1.12.2",
            "https://dl.reformcloud.systems/mcversions/paper/paper-1.12.2.jar", 1),
    PAPER_1_13_2("Paper 1.13.2", "1.13.2",
            "https://dl.reformcloud.systems/mcversions/paper/paper-1.13.2jar", 1),
    PAPER_1_14_4("Paper 1.14.4", "1.14.4",
            "https://dl.reformcloud.systems/mcversions/paper/paper-1.14.4.jar", 1),

    /**
     * SpongeVanilla Versions
     */
    SPONGEVANILLA_1_8_9("SpongeVanilla 1.8.9", "1.8.9",
            "https://dl.reformcloud.systems/mcversions/spongevanilla/spongevanilla-1.8.9.jar", 1),
    SPONGEVANILLA_1_9_4("SpongeVanilla 1.9.4", "1.9.4",
            "https://dl.reformcloud.systems/mcversions/spongevanilla/spongevanilla-1.9.4.jar", 1),
    SPONGEVANILLA_1_10_2("SpongeVanilla 1.10.2", "1.10.2",
            "https://dl.reformcloud.systems/mcversions/spongevanilla/spongevanilla-1.10.2.jar", 1),
    SPONGEVANILLA_1_11_2("SpongeVanilla 1.11.2", "1.11.2",
            "https://dl.reformcloud.systems/mcversions/spongevanilla/spongevanilla-1.11.2.jar", 1),
    SPONGEVANILLA_1_12_2("SpongeVanilla 1.12.2", "1.12.2",
            "https://dl.reformcloud.systems/mcversions/spongevanilla/spongevanilla-1.12.2.jar", 1),
    /**
     * SpongeForge Versions
     */
    SPONGEFORGE_1_8_9("SpongeForge 1.8.9", "1.8.9",
            "https://dl.reformcloud.systems/mcversions/forge/sponge-1.8.9.zip", 1),
    SPONGEFORGE_1_10_2("SpongeForge 1.10.2", "1.10.2",
            "https://dl.reformcloud.systems/mcversions/forge/sponge-1.10.2.zip", 1),
    SPONGEFORGE_1_11_2("SpongeForge 1.11.2", "1.11.2",
            "https://dl.reformcloud.systems/mcversions/forge/sponge-1.11.2.zip", 1),
    SPONGEFORGE_1_12_2("SpongeForge 1.12.2", "1.12.2",
            "https://dl.reformcloud.systems/mcversions/forge/sponge-1.12.2.zip", 1),
    /**
     * TacoSpigot Versions
     */
    TACO_1_8_8("Taco 1.8.8", "1.8.8",
            "https://dl.reformcloud.systems/mcversions/taco/tacospigot-1.8.8.jar", 1),
    TACO_1_11_2("Taco 1.11.2", "1.11.2",
            "https://dl.reformcloud.systems/mcversions/taco/tacospigot-1.11.2.jar", 1),
    TACO_1_12_2("Taco 1.12.2", "1.12.2",
            "https://dl.reformcloud.systems/mcversions/taco/tacospigot-1.12.2.jar", 1),
    /**
     * TorchSpigot Versions
     */
    TORCH_1_8_8("Torch 1.8.8", "1.8.8",
            "https://dl.reformcloud.systems/mcversions/torch/Torch-1.8.8.jar", 1),
    TORCH_1_9_4("Torch 1.9.4", "1.9.4",
            "https://dl.reformcloud.systems/mcversions/torch/Torch-1.9.4.jar", 1),
    TORCH_1_11_2("Torch 1.11.2", "1.11.2",
            "https://dl.reformcloud.systems/mcversions/torch/Torch-1.11.2.jar", 1),
    /**
     * Hose Versions
     */
    HOSE_1_8_8("Hose 1.8.8", "1.8.8",
            "https://dl.reformcloud.systems/mcversions/hose/hose-1.8.8.jar", 1),
    HOSE_1_9_4("Hose 1.9.4", "1.9.4",
            "https://dl.reformcloud.systems/mcversions/hose/hose-1.9.4.jar", 1),
    HOSE_1_10_2("Hose 1.10.2", "1.10.2",
            "https://dl.reformcloud.systems/mcversions/hose/hose-1.10.2.jar", 1),
    HOSE_1_11_2("Hose 1.11.2", "1.11.2",
            "https://dl.reformcloud.systems/mcversions/hose/hose-1.11.2.jar", 1),
    /**
     * Glowstone Versions
     */
    GLOWSTONE_1_10_2("Glowstone 1.10_2", "1.10.2",
            "https://dl.reformcloud.systems/mcversions/glowstone/glowstone-1.10.2.jar", 1),
    GLOWSTONE_1_12_2("Glowstone 1.12.2", "1.12.2",
            "https://dl.reformcloud.systems/mcversions/glowstone/glowstone-1.12.2.jar", 1),
    /**
     * Akarin Versions
     */
    AKARIN_1_12_2("Akarin 1.12.2", "1.12.2",
            "https://github.com/Akarin-project/Akarin/releases/download/1.12.2-R0.4.2/akarin-1.12.2.jar", 1),
    /**
     * Proxy versions
     */
    BUNGEECORD("BungeeCord", "1.8-1.14",
            "https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar", 2),
    WATERFALL("Waterfall", "1.8-1.14",
            "https://papermc.io/ci/job/Waterfall/lastSuccessfulBuild/artifact/Waterfall-Proxy/bootstrap/target/Waterfall.jar", 2),
    HEXACORD("HexaCord", "1.7-1.14",
            "https://github.com/HexagonMC/BungeeCord/releases/download/v246/BungeeCord.jar", 2),
    TRAVERTINE("Travertine", "1.7-1.14",
            "https://papermc.io/ci/job/Travertine/lastSuccessfulBuild/artifact/Travertine-Proxy/bootstrap/target/Travertine.jar", 2),
    VELOCITY("Velocity", "1.8-1.14",
            "https://ci.velocitypowered.com/job/velocity/lastSuccessfulBuild/artifact/proxy/build/libs/velocity-proxy-1.0.4-SNAPSHOT-all.jar", 2),
    /**
     * MCPE server
     */
    NUKKIT_X("NukkitX", "1.0",
            "https://ci.nukkitx.com/job/NukkitX/job/Nukkit/job/master/lastStableBuild/artifact/target/nukkit-1.0-SNAPSHOT.jar", 3),
    /**
     * Waterdog as Java Proxy
     */
    WATERDOG("Waterdog", "1.8-1.14",
            "https://ci.codemc.org/job/yesdog/job/Waterdog/lastSuccessfulBuild/artifact/Waterfall-Proxy/bootstrap/target/Waterdog.jar", 2),
    /**
     * Waterdog as McPE Proxy
     */
    WATERDOG_PE("Waterdog", "1.8-1.14",
            "https://ci.codemc.org/job/yesdog/job/Waterdog/lastSuccessfulBuild/artifact/Waterfall-Proxy/bootstrap/target/Waterdog.jar", 4);

    private static final TreeMap<String, Version> JAVA_SERVER_PROVIDERS = new TreeMap<>();

    private static final TreeMap<String, Version> JAVA_PROXY_PROVIDERS = new TreeMap<>();

    private static final TreeMap<String, Version> POCKET_SERVER_PROVIDERS = new TreeMap<>();

    private static final TreeMap<String, Version> POCKET_PROXY_PROVIDERS = new TreeMap<>();

    public static final Deque<String> AVAILABLE_JAVA_SERVER_VERSIONS = new ConcurrentLinkedDeque<>();

    public static final Deque<String> AVAILABLE_JAVA_PROXY_VERSIONS = new ConcurrentLinkedDeque<>();

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

        AVAILABLE_POCKET_PROXY_VERSIONS.add("1.0");

        AVAILABLE_POCKET_SERVER_VERSIONS.add("1.0");

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
        DownloadHelper.downloadAndDisconnect(version.url, "reformcloud/files/" + format(version));
    }

    public static String format(Version version) {
        return version.name.toLowerCase().replace(" ", "-") + ".jar";
    }
}
