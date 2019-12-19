package systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.file;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.utility.maps.AbsentMap;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

public final class FileDatabase extends Database<Path> {

  private String table;

  private final Map<String, DatabaseReader> perTableReader = new AbsentMap<>();

  @Override
  public void connect(@Nonnull String host, int port, @Nonnull String userName,
                      @Nonnull String password, @Nonnull String table) {
    this.table = "reformcloud/.database/" + table;
  }

  @Override
  public boolean isConnected() {
    return true;
  }

  @Override
  public void reconnect() {}

  @Override
  public void disconnect() {}

  @Override
  public boolean createDatabase(String name) {
    SystemHelper.createDirectory(Paths.get(table + "/" + name));
    return true;
  }

  @Override
  public boolean deleteDatabase(String name) {
    SystemHelper.deleteDirectory(Paths.get(table + "/" + name));
    perTableReader.remove(name);
    return true;
  }

  @Override
  public DatabaseReader createForTable(String table) {
    return perTableReader.putIfAbsent(table, new DatabaseReader() {
      @Nonnull
      @Override
      public Task<JsonConfiguration> find(@Nonnull String key) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
          for (File file : Objects.requireNonNull(
                   new File(FileDatabase.this.table + "/" + table)
                       .listFiles(
                           pathname
                           -> pathname.isFile() &&
                                  pathname.getName().endsWith(".json")))) {
            if (file.getName().startsWith(key)) {
              task.complete(JsonConfiguration.read(file));
              return;
            }
          }

          task.complete(null);
        });
        return task;
      }

      @Nonnull
      @Override
      public Task<JsonConfiguration> findIfAbsent(@Nonnull String identifier) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
          for (File file : Objects.requireNonNull(
                   new File(FileDatabase.this.table + "/" + table)
                       .listFiles(
                           pathname
                           -> pathname.isFile() &&
                                  pathname.getName().endsWith(".json")))) {
            String[] split = file.getName().split("-");
            if (split.length == 2 &&
                split[1].replace(".json", "").equals(identifier)) {
              task.complete(JsonConfiguration.read(file));
              return;
            }
          }

          task.complete(null);
        });
        return task;
      }

      @Nonnull
      @Override
      public Task<JsonConfiguration> insert(@Nonnull String key,
                                            String identifier,
                                            @Nonnull JsonConfiguration data) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
          JsonConfiguration configuration =
              find(key).getUninterruptedly(TimeUnit.SECONDS, 5);
          if (configuration == null) {
            data.write(Paths.get(FileDatabase.this.table + "/" + table + "/" +
                                 key + "-" + identifier + ".json"));
            task.complete(data);
          } else {
            task.complete(configuration);
          }
        });
        return task;
      }

      @Nonnull
      @Override
      public Task<Boolean> update(@Nonnull String key,
                                  @Nonnull JsonConfiguration newData) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
          for (File file : Objects.requireNonNull(
                   new File(FileDatabase.this.table + "/" + table)
                       .listFiles(
                           pathname
                           -> pathname.isFile() &&
                                  pathname.getName().endsWith(".json")))) {
            if (file.getName().startsWith(key)) {
              newData.write(file);
              task.complete(true);
              return;
            }
          }

          task.complete(false);
        });
        return task;
      }

      @Nonnull
      @Override
      public Task<Boolean> updateIfAbsent(@Nonnull String identifier,
                                          @Nonnull JsonConfiguration newData) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
          for (File file : Objects.requireNonNull(
                   new File(FileDatabase.this.table + "/" + table)
                       .listFiles(
                           pathname
                           -> pathname.isFile() &&
                                  pathname.getName().endsWith(".json")))) {
            String[] split = file.getName().split("-");
            if (split.length == 2 &&
                split[1].replace(".json", "").equals(identifier)) {
              newData.write(file);
              task.complete(true);
              return;
            }
          }

          task.complete(false);
        });
        return task;
      }

      @Nonnull
      @Override
      public Task<Void> remove(@Nonnull String key) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
          for (File file : Objects.requireNonNull(
                   new File(FileDatabase.this.table + "/" + table)
                       .listFiles(
                           pathname
                           -> pathname.isFile() &&
                                  pathname.getName().endsWith(".json")))) {
            if (file.getName().startsWith(key)) {
              SystemHelper.deleteFile(file);
              break;
            }
          }

          task.complete(null);
        });
        return task;
      }

      @Nonnull
      @Override
      public Task<Void> removeIfAbsent(@Nonnull String identifier) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
          for (File file : Objects.requireNonNull(
                   new File(FileDatabase.this.table + "/" + table)
                       .listFiles(
                           pathname
                           -> pathname.isFile() &&
                                  pathname.getName().endsWith(".json")))) {
            String[] split = file.getName().split("-");
            if (split.length == 2 &&
                split[1].replace(".json", "").equals(identifier)) {
              SystemHelper.deleteFile(file);
              return;
            }
          }

          task.complete(null);
        });
        return task;
      }

      @Nonnull
      @Override
      public Task<Boolean> contains(@Nonnull String key) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
          for (File file : Objects.requireNonNull(
                   new File(FileDatabase.this.table + "/" + table)
                       .listFiles(
                           pathname
                           -> pathname.isFile() &&
                                  pathname.getName().endsWith(".json")))) {
            if (file.getName().startsWith(key)) {
              task.complete(true);
              return;
            }
          }

          task.complete(false);
        });
        return task;
      }

      @Nonnull
      @Override
      public Task<Integer> size() {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
          int size = Objects
                         .requireNonNull(
                             new File(FileDatabase.this.table + "/" + table)
                                 .listFiles(pathname
                                            -> pathname.isFile() &&
                                                   pathname.getName().endsWith(
                                                       ".json")))
                         .length;
          task.complete(size);
        });
        return task;
      }

      @Nonnull
      @Override
      public String getName() {
        return table;
      }

      @Override
      public Iterator<JsonConfiguration> iterator() {
        List<JsonConfiguration> list = new ArrayList<>();
        for (File file : Objects.requireNonNull(
                 new File(FileDatabase.this.table + "/" + table)
                     .listFiles(pathname
                                -> pathname.isFile() &&
                                       pathname.getName().endsWith(".json")))) {
          list.add(JsonConfiguration.read(file));
        }

        return list.iterator();
      }
    });
  }

  @Override
  public Path get() {
    return Paths.get(table);
  }
}
