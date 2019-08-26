package de.klaro.reformcloud2.executor.api.common.patch.basic;

import com.google.gson.reflect.TypeToken;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.language.LanguageManager;
import de.klaro.reformcloud2.executor.api.common.patch.Patch;
import de.klaro.reformcloud2.executor.api.common.patch.Patcher;
import de.klaro.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import de.klaro.reformcloud2.executor.api.common.utility.system.SystemHelper;
import de.klaro.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class DefaultPatcher implements Patcher {

    private static final Path PATH = Paths.get("reformcloud/.update/internal.json");

    private static final String UPDATE_SERVER_URL = "http://external.reformcloud.systems:1550/";

    private final List<Patch> patches = new ArrayList<>();

    private final JsonConfiguration jsonConfiguration;

    public DefaultPatcher() {
        SystemHelper.createDirectory(Paths.get("reformcloud/.update"));
        if (!Files.exists(PATH)) {
            new JsonConfiguration()
                    .add("lastCheck", -1L)
                    .add("pausedTo", -1L)
                    .write(PATH);
        }

        jsonConfiguration = JsonConfiguration.read(PATH);
        read();

        if (pausedTo != -1 && System.currentTimeMillis() > pausedTo) {
            pausedTo = -1;
            write();
        }

        if (lastCheck == -1L) {
            loadPatches(-1L);
        }

        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    AbsoluteThread.sleep(TimeUnit.MINUTES, 10);
                    loadPatches(lastCheck);
                }
            }
        });
    }

    private long pausedTo;

    private long lastCheck;

    @Override
    public List<Patch> patches() {
        return Collections.unmodifiableList(patches);
    }

    @Override
    public boolean hasPatches() {
        return patches.size() != 0;
    }

    @Override
    public void pausePatches(long time, TimeUnit timeUnit) {
        this.pausedTo = System.currentTimeMillis() + timeUnit.toMillis(time);
        write();
    }

    @Override
    public void resumePatches() {
        this.pausedTo = -1L;
        write();
    }

    @Override
    public void loadPatches(long lastCheck) {
        if (checkAccess()) {
            if (lastCheck != -1L) {
                DownloadHelper.openConnection(UPDATE_SERVER_URL + "requestpatches", Collections.singletonMap(
                        "-XLastCheck", Long.toString(lastCheck)
                ), new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream inputStream) {
                        JsonConfiguration jsonConfiguration = new JsonConfiguration(inputStream);
                        if (jsonConfiguration.getBoolean("success")) {
                            jsonConfiguration.get("result", new TypeToken<List<DefaultPatch>>() {}).forEach(new Consumer<DefaultPatch>() {
                                @Override
                                public void accept(DefaultPatch defaultPatch) {
                                    patches.add(defaultPatch);
                                }
                            });
                        }
                    }
                });
            }

            this.lastCheck = System.currentTimeMillis();
            write();
        }
    }

    @Override
    public void doPatches() {
        if (checkAccess()) {
            new ArrayList<>(patches).forEach(new Consumer<Patch>() {
                @Override
                public void accept(Patch patch) {
                    final long current = System.currentTimeMillis();
                    patches.remove(patch);
                    String file = patch.patchNote().getName() + "-" + patch.patchNote().newVersion() + ".jar";

                    System.out.println(LanguageManager.get("patch-apply", patch.patchNote().getName(),
                            patch.patchNote().newVersion()));

                    DownloadHelper.openConnection(UPDATE_SERVER_URL + "downloadpatch", Collections.singletonMap(
                            "-XFileName", patch.fileName()
                    ), new Consumer<InputStream>() {
                        @Override
                        public void accept(InputStream inputStream) {
                            SystemHelper.doCopy(inputStream, Paths.get(
                                    "reformcloud/.update/" + patch.patchNote().getName() + "-" + patch.patchNote().newVersion() + ".jar"
                            ));
                        }
                    });
                    System.out.println(patch.patchNote().updateMessage());

                    try {
                        Process process = Runtime.getRuntime().exec("java -jar " + file, null, new File("reformcloud/.update"));
                        process.waitFor();
                        process.destroyForcibly().destroy();
                    } catch (final IOException | InterruptedException ex) {
                        ex.printStackTrace();
                    }

                    System.out.println(LanguageManager.get("patch-apply-done", patch.patchNote().getName(),
                            Long.toString(System.currentTimeMillis() - current)));
                }
            });
        }
    }

    private void read() {
        this.lastCheck = jsonConfiguration.getOrDefault("lastCheck", -1L);
        this.pausedTo = jsonConfiguration.getOrDefault("pausedTo", -1L);
    }

    private void write() {
        jsonConfiguration.add("lastCheck", lastCheck).add("pausedTo", pausedTo).write(PATH);
    }

    private boolean checkAccess() {
        if (pausedTo == -1L) {
            return true;
        }

        if (pausedTo > System.currentTimeMillis()) {
            return false;
        }

        pausedTo = -1L;
        write();
        return true;
    }
}
