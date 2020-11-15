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
package systems.reformcloud.reformcloud2.executor.api.groups.template.version;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public final class Versions {
    private static final Map<String, Version> VERSIONS = new ConcurrentHashMap<>();
    // ----
    // JAVA SERVERS
    // ----
    // spigot versions
    public static final Version SPIGOT_1_8 = javaServer("SPIGOT_1_8", "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.8.jar", false);
    public static final Version SPIGOT_1_9_4 = javaServer("SPIGOT_1_9_4", "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.9.4.jar");
    public static final Version SPIGOT_1_10_2 = javaServer("SPIGOT_1_10_2", "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.10.2.jar");
    public static final Version SPIGOT_1_11_2 = javaServer("SPIGOT_1_11_2", "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.11.2.jar");
    public static final Version SPIGOT_1_12_2 = javaServer("SPIGOT_1_12_2", "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.12.2.jar");
    public static final Version SPIGOT_1_13_2 = javaServer("SPIGOT_1_13_2", "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.13.2.jar");
    public static final Version SPIGOT_1_14_4 = javaServer("SPIGOT_1_14_4", "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.14.4.jar");
    public static final Version SPIGOT_1_15_2 = javaServer("Spigot 1.15.2", "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.15.2.jar");
    public static final Version SPIGOT_1_16_4 = javaServer("SPIGOT_1_16_4", "https://dl.reformcloud.systems/mcversions/spigots/spigot-1.16.4.jar");
    // paper versions
    public static final Version PAPER_1_8_8 = javaServer("PAPER_1_8_8", "https://dl.reformcloud.systems/mcversions/paper/paper-1.8.8.jar", false);
    public static final Version PAPER_1_9_4 = javaServer("PAPER_1_9_4", "https://dl.reformcloud.systems/mcversions/paper/paper-1.9.4.jar");
    public static final Version PAPER_1_10_2 = javaServer("PAPER_1_10_2", "https://dl.reformcloud.systems/mcversions/paper/paper-1.10.2.jar");
    public static final Version PAPER_1_11_2 = javaServer("PAPER_1_11_2", "https://dl.reformcloud.systems/mcversions/paper/paper-1.11.2.jar");
    public static final Version PAPER_1_12_2 = javaServer("PAPER_1_12_2", "https://dl.reformcloud.systems/mcversions/paper/paper-1.12.2.jar");
    public static final Version PAPER_1_13_2 = javaServer("PAPER_1_13_2", "https://dl.reformcloud.systems/mcversions/paper/paper-1.13.2.jar");
    public static final Version PAPER_1_14_4 = javaServer("PAPER_1_14_4", "https://dl.reformcloud.systems/mcversions/paper/paper-1.14.4.jar");
    public static final Version PAPER_1_15_2 = javaServer("PAPER_1_15_2", "https://dl.reformcloud.systems/mcversions/paper/paper-1.15.2.jar");
    public static final Version PAPER_1_16_4 = javaServer("PAPER_1_16_4", "https://dl.reformcloud.systems/mcversions/paper/paper-1.16.4.jar");
    // tuinity versions
    public static final Version TUINITY_1_15_2 = javaServer("TUINITY_1_15_2", "https://dl.reformcloud.systems/mcversions/tuinity/tuinity-1.15.2.jar");
    public static final Version TUINITY_1_16_4 = javaServer("TUINITY_1_16_4", "https://dl.reformcloud.systems/mcversions/tuinity/tuinity-1.16.4.jar");
    // sponge vanilla
    public static final Version SPONGEVANILLA_1_11_2 = javaServer("SPONGEVANILLA_1_11_2", "https://dl.reformcloud.systems/mcversions/spongevanilla/spongevanilla-1.11.2.jar");
    public static final Version SPONGEVANILLA_1_12_2 = javaServer("SPONGEVANILLA_1_12_2", "https://dl.reformcloud.systems/mcversions/spongevanilla/spongevanilla-1.12.2.jar");
    // sponge forge
    public static final Version SPONGEFORGE_1_10_2 = spongeForgeServer("SPONGEFORGE_1_10_2", "https://dl.reformcloud.systems/mcversions/forge/sponge-1.10.2.zip");
    public static final Version SPONGEFORGE_1_11_2 = spongeForgeServer("SPONGEFORGE_1_11_2", "https://dl.reformcloud.systems/mcversions/forge/sponge-1.11.2.zip");
    public static final Version SPONGEFORGE_1_12_2 = spongeForgeServer("SPONGEFORGE_1_12_2", "https://dl.reformcloud.systems/mcversions/forge/sponge-1.12.2.zip");
    // tacospigot versions
    public static final Version TACO_1_8_8 = javaServer("TACO_1_8_8", "https://dl.reformcloud.systems/mcversions/taco/tacospigot-1.8.8.jar", false);
    public static final Version TACO_1_11_2 = javaServer("TACO_1_11_2", "https://dl.reformcloud.systems/mcversions/taco/tacospigot-1.11.2.jar");
    public static final Version TACO_1_12_2 = javaServer("TACO_1_12_2", "https://dl.reformcloud.systems/mcversions/taco/tacospigot-1.12.2.jar");
    // torchspigot versions
    public static final Version TORCH_1_8_8 = javaServer("TORCH_1_8_8", "https://dl.reformcloud.systems/mcversions/torch/Torch-1.8.8.jar", false);
    public static final Version TORCH_1_9_4 = javaServer("TORCH_1_9_4", "https://dl.reformcloud.systems/mcversions/torch/Torch-1.9.4.jar");
    public static final Version TORCH_1_11_2 = javaServer("TORCH_1_11_2", "https://dl.reformcloud.systems/mcversions/torch/Torch-1.11.2.jar");
    // hose versions
    public static final Version HOSE_1_8_8 = javaServer("HOSE_1_8_8", "https://dl.reformcloud.systems/mcversions/hose/hose-1.8.8.jar", false);
    public static final Version HOSE_1_9_4 = javaServer("HOSE_1_9_4", "https://dl.reformcloud.systems/mcversions/hose/hose-1.9.4.jar");
    public static final Version HOSE_1_10_2 = javaServer("HOSE_1_10_2", "https://dl.reformcloud.systems/mcversions/hose/hose-1.10.2.jar");
    public static final Version HOSE_1_11_2 = javaServer("HOSE_1_11_2", "https://dl.reformcloud.systems/mcversions/hose/hose-1.11.2.jar");
    // glowstone versions
    public static final Version GLOWSTONE_1_10_2 = javaServer("GLOWSTONE_1_10_2", "https://dl.reformcloud.systems/mcversions/glowstone/glowstone-1.10.2.jar");
    public static final Version GLOWSTONE_1_12_2 = javaServer("GLOWSTONE_1_12_2", "https://dl.reformcloud.systems/mcversions/glowstone/glowstone-1.12.2.jar");
    // akarin versions
    public static final Version AKARIN_1_12_2 = javaServer("AKARIN_1_12_2", "https://github.com/Akarin-project/Akarin/releases/download/1.12.2-R0.4.2/akarin-1.12.2.jar");
    // ----
    // JAVA PROXIES
    // ----
    // bungeecord & forks
    public static final Version BUNGEECORD = javaProxy("BUNGEECORD", "https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar");
    public static final Version WATERFALL = javaProxy("WATERFALL", "https://papermc.io/ci/job/Waterfall/lastSuccessfulBuild/artifact/Waterfall-Proxy/bootstrap/target/Waterfall.jar");
    public static final Version HEXACORD = javaProxy("HEXACORD", "https://github.com/HexagonMC/BungeeCord/releases/download/v246/BungeeCord.jar");
    public static final Version TRAVERTINE = javaProxy("TRAVERTINE", "https://papermc.io/ci/job/Travertine/lastSuccessfulBuild/artifact/Travertine-Proxy/bootstrap/target/Travertine.jar");
    public static final Version WATERDOG = javaProxy("WATERDOG", "https://ci.codemc.org/job/yesdog/job/Waterdog/lastSuccessfulBuild/artifact/Waterfall-Proxy/bootstrap/target/Waterdog.jar");
    // velocity
    public static final Version VELOCITY = javaProxy("VELOCITY", "https://versions.velocitypowered.com/download/latest.jar");
    // ----
    // POCKET SERVERS
    // ----
    // CloudBurstMC/nukkit & CloudBurstMC/server
    public static final Version NUKKIT_X = pocketServer("NUKKIT_X", "https://ci.nukkitx.com/job/NukkitX/job/Nukkit/job/master/lastStableBuild/artifact/target/nukkit-1.0-SNAPSHOT.jar");
    public static final Version CLOUDBURST = pocketServer("CLOUDBURST", "https://ci.nukkitx.com/job/NukkitX/job/Server/job/bleeding/lastStableBuild/artifact/target/Cloudburst.jar");
    // ----
    // POCKET PROXIES
    // ----
    public static final Version WATERDOG_PE = pocketProxy("WATERDOG_PE", "https://ci.codemc.org/job/yesdog/job/Waterdog/lastSuccessfulBuild/artifact/Waterfall-Proxy/bootstrap/target/Waterdog.jar");

    private Versions() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static Optional<Version> getByName(@NotNull String name) {
        return Optional.ofNullable(VERSIONS.get(name.toUpperCase()));
    }

    private static Version javaServer(String versionName, String downloadUrl) {
        return javaServer(versionName, downloadUrl, true);
    }

    private static Version javaServer(String versionName, String downloadUrl, boolean nativeTransportSupported) {
        return version(versionName, downloadUrl, VersionType.JAVA_SERVER, 41000, nativeTransportSupported);
    }

    private static Version javaProxy(String versionName, String downloadUrl) {
        return version(versionName, downloadUrl, VersionType.JAVA_PROXY, 25565, true);
    }

    private static Version pocketServer(String versionName, String downloadUrl) {
        return version(versionName, downloadUrl, VersionType.POCKET_SERVER, 41000, true);
    }

    @SuppressWarnings("SameParameterValue")
    private static Version pocketProxy(String versionName, String downloadUrl) {
        return version(versionName, downloadUrl, VersionType.POCKET_PROXY, 19132, true);
    }

    private static Version version(String versionName, String downloadUrl, VersionType versionType, int defaultStartPort, boolean nativeTransportSupported) {
        Version version = Version.version(versionName, downloadUrl, VersionInstaller.DOWNLOADING, versionType, defaultStartPort, nativeTransportSupported);
        VERSIONS.put(versionName.toUpperCase(), version);
        return version;
    }

    private static Version spongeForgeServer(String versionName, String downloadUrl) {
        Version version = Version.version(versionName, downloadUrl, VersionInstaller.SPONGE, VersionType.JAVA_SERVER, 41000, true);
        VERSIONS.put(versionName.toUpperCase(), version);
        return version;
    }
}
