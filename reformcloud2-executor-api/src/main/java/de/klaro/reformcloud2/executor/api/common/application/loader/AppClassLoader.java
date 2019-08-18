package de.klaro.reformcloud2.executor.api.common.application.loader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public final class AppClassLoader extends URLClassLoader {

    private static final Set<AppClassLoader> allLoaders = new CopyOnWriteArraySet<>();

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public AppClassLoader(URL[] urls) {
        super(urls);
        allLoaders.add(this);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (final ClassNotFoundException ex) {
            for (AppClassLoader allLoader : allLoaders) {
                if (allLoader != this) {
                    try {
                        return allLoader.loadClass(name, resolve);
                    } catch (final ClassNotFoundException ignored) {
                    }
                }
            }
        }

        throw new ClassNotFoundException(name);
    }
}
