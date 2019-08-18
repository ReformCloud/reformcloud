package de.klaro.reformcloud2.executor.api.common.dependency;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public abstract class DependencyLoader {

    public static void doLoad() {
        DependencyLoader dependencyLoader = new DefaultDependencyLoader();
        dependencyLoader.loadDependencies();
        dependencyLoader.addDependencies();
    }

    void addURL(URL url) {
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        try {
            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURL.setAccessible(true);
            addURL.invoke(urlClassLoader, url);
        } catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    public abstract void loadDependencies();

    public abstract void addDependencies();

    public abstract URL loadDependency(Dependency dependency);

    public abstract void addDependency(URL depend);
}
