package systems.reformcloud.reformcloud2.executor.api.common.registry.basic;

import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.registry.Registry;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;

public class RegistryBuilder {

  private RegistryBuilder() { throw new UnsupportedOperationException(); }

  public static Registry newRegistry(Path operatingFolder) {
    return new RegistryImpl(operatingFolder);
  }

  private static final class RegistryImpl implements Registry {

    private RegistryImpl(Path folder) {
      this.folder = folder;
      SystemHelper.createDirectory(folder);
    }

    private final Path folder;

    @Nonnull
    @Override
    public <T> T createKey(@Nonnull String keyName, @Nonnull T t) {
      Path target = Paths.get(folder + "/" + keyName + ".json");
      if (Files.exists(target)) {
        return null;
      }

      new JsonConfiguration().add("key", t).write(target);
      return t;
    }

    @Override
    public <T> T getKey(@Nonnull String keyName) {
      Path target = Paths.get(folder + "/" + keyName + ".json");
      if (Files.exists(target)) {
        return null;
      }

      return JsonConfiguration.read(target).get("key", new TypeToken<T>() {});
    }

    @Override
    public void deleteKey(@Nonnull String key) {
      Path target = Paths.get(folder + "/" + key + ".json");
      SystemHelper.deleteFile(target.toFile());
    }

    @Override
    public <T> T updateKey(@Nonnull String key, @Nonnull T newValue) {
      Path target = Paths.get(folder + "/" + key + ".json");
      if (!Files.exists(target)) {
        return null;
      }

      new JsonConfiguration().add("key", newValue).write(target);
      return newValue;
    }

    @Nonnull
    @Override
    public <T> Collection<T>
    readKeys(@Nonnull Function<JsonConfiguration, T> function) {
      if (!Files.exists(folder)) {
        return Collections.emptyList();
      }

      File[] files = folder.toFile().listFiles();
      if (files == null) {
        return Collections.emptyList();
      }

      final Collection<T> out = new ArrayList<>();
      Arrays.stream(files)
          .map(file -> {
            JsonConfiguration configuration = JsonConfiguration.read(file);
            return function.apply(configuration);
          })
          .forEach(out::add);
      return out;
    }
  }
}
