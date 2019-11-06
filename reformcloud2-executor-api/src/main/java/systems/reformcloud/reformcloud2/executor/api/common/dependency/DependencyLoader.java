package systems.reformcloud.reformcloud2.executor.api.common.dependency;

import systems.reformcloud.reformcloud2.runner.classloading.RunnerClassLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URL;

public abstract class DependencyLoader {

    /**
     * Loads all default dependencies
     */
    public static void doLoad() {
        DependencyLoader dependencyLoader = new DefaultDependencyLoader();
        dependencyLoader.loadDependencies();
        dependencyLoader.addDependencies();
    }

    /**
     * Appends an url to the class loader search
     *
     * @param url The url which should be appended
     */
    void addURL(URL url) {
        RunnerClassLoader urlClassLoader = (RunnerClassLoader) Thread.currentThread().getContextClassLoader();
        urlClassLoader.addURL(url);
    }

    /**
     * Loads all dependencies
     */
    public abstract void loadDependencies();

    /**
     * Adds all dependencies to the class loader search
     */
    public abstract void addDependencies();

    /**
     * Loads a specific dependency
     *
     * @param dependency The dependency which should be loaded
     * @return The file place of the dependency ad {@link URL}
     */
    @Nullable
    public abstract URL loadDependency(@Nonnull Dependency dependency);

    /**
     * Adds the dependency location to the class loader search
     *
     * @param depend The {@link URL} to the place of the dependency
     */
    public abstract void addDependency(@Nonnull URL depend);
}
