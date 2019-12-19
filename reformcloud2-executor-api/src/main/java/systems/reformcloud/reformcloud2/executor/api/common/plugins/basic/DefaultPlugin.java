package systems.reformcloud.reformcloud2.executor.api.common.plugins.basic;

import com.google.gson.reflect.TypeToken;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin;

public final class DefaultPlugin extends Plugin {

  public static final TypeToken<DefaultPlugin> TYPE_TOKEN =
      new TypeToken<DefaultPlugin>() {};

  public DefaultPlugin(String version, String author, String main,
                       List<String> depends, List<String> softpends,
                       boolean enabled, String name) {
    this.version = version;
    this.author = author;
    this.main = main;
    this.depends = depends;
    this.softpends = softpends;
    this.enabled = enabled;
    this.name = name;
  }

  private final String version;

  private final String author;

  private final String main;

  private final List<String> depends;

  private final List<String> softpends;

  private final boolean enabled;

  private final String name;

  @Nonnull
  @Override
  public String version() {
    return version;
  }

  @Nullable
  @Override
  public String author() {
    return author;
  }

  @Nonnull
  @Override
  public String main() {
    return main;
  }

  @Override
  public List<String> depends() {
    return depends;
  }

  @Override
  public List<String> softpends() {
    return softpends;
  }

  @Override
  public boolean enabled() {
    return enabled;
  }

  @Nonnull
  @Override
  public String getName() {
    return name;
  }
}
