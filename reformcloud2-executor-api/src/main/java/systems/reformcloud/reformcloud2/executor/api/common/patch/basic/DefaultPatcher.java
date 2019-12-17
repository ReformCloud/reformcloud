package systems.reformcloud.reformcloud2.executor.api.common.patch.basic;

import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.patch.Patch;
import systems.reformcloud.reformcloud2.executor.api.common.patch.Patcher;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;

public final class DefaultPatcher implements Patcher {

  private static final String[] COMMAND =
      Arrays.asList("java", "-jar", "patch.jar").toArray(new String[0]);

  private static final File FILE = new File("patches");

  private static final Path PATH =
      Paths.get("reformcloud/.update/internal.json");

  private static final String UPDATE_SERVER_URL =
      "http://external.reformcloud.systems:1550/";

  private static final DateFormat DATE_FORMAT =
      new SimpleDateFormat("dd.MM.yyyy kk:mm:ss");

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

    if (Boolean.getBoolean("reformcloud.patcher.disable")) {
      CompletableFuture.runAsync(() -> {
        while (!Thread.currentThread().isInterrupted()) {
          AbsoluteThread.sleep(TimeUnit.MINUTES, 10);
          loadPatches(lastCheck);
        }
      });
    }
  }

  private long pausedTo;

  private long lastCheck;

  @Nonnull
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
        DownloadHelper.openConnection(
            UPDATE_SERVER_URL + "patches",
            Collections.singletonMap("-XLastCheck", Long.toString(lastCheck)),
            inputStream -> {
              JsonConfiguration jsonConfiguration =
                  new JsonConfiguration(inputStream);
              if (jsonConfiguration.getBoolean("success")) {
                Collection<DefaultPatch> patches = jsonConfiguration.get(
                    "updates", new TypeToken<Collection<DefaultPatch>>() {});
                if (patches.size() != 0) {
                  this.patches.addAll(patches);
                  System.out.println(
                      LanguageManager.get("patches-found", patches.size()));
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
    SystemHelper.createDirectory(FILE.toPath());

    if (checkAccess()) {
      synchronized (this.patches) {
        Links.newList(this.patches).forEach(e -> {
          try {
            final long startTime = System.currentTimeMillis();
            System.out.println(
                LanguageManager.get("patch-apply", e.getDownloadURL(),
                                    DATE_FORMAT.format(e.getReleaseDate())));

            DownloadHelper.downloadAndDisconnect(e.getDownloadURL(),
                                                 "patches/patch.jar");
            Process process =
                new ProcessBuilder(COMMAND).inheritIO().directory(FILE).start();

            process.waitFor();
            process.destroy();

            SystemHelper.deleteFile(new File("patches/patch.jar"));

            System.out.println(
                LanguageManager.get("patch-apply-done", e.getDownloadURL(),
                                    System.currentTimeMillis() - startTime));
          } catch (final InterruptedException | IOException ex) {
            ex.printStackTrace();
          }
        });
      }
    }
  }

  private void read() {
    this.lastCheck = jsonConfiguration.getOrDefault("lastCheck", -1L);
    this.pausedTo = jsonConfiguration.getOrDefault("pausedTo", -1L);
  }

  private void write() {
    jsonConfiguration.add("lastCheck", lastCheck)
        .add("pausedTo", pausedTo)
        .write(PATH);
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
