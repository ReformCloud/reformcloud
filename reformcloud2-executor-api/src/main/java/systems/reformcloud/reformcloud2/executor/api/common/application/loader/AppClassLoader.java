package systems.reformcloud.reformcloud2.executor.api.common.application.loader;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public final class AppClassLoader extends URLClassLoader {

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public AppClassLoader(@NotNull URL[] urls, @NotNull ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
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
