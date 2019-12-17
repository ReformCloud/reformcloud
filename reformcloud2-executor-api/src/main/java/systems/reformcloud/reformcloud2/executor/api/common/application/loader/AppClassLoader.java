package systems.reformcloud.reformcloud2.executor.api.common.application.loader;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import javax.annotation.Nonnull;

public final class AppClassLoader extends URLClassLoader {

  static { ClassLoader.registerAsParallelCapable(); }

  public AppClassLoader(@Nonnull URL[] urls, @Nonnull ClassLoader parent) {
    super(urls, parent);
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve)
      throws ClassNotFoundException {
    return super.loadClass(name, resolve);
  }

  @Override
  public void close() {
    try {
      super.close();
    } catch (final IOException ignored) {
    }
  }
}
