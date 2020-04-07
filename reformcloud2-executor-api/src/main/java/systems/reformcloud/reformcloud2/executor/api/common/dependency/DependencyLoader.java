package systems.reformcloud.reformcloud2.executor.api.common.dependency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.runner.RunnerClassLoader;

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
    void addURL(@NotNull URL url) {
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
    public abstract URL loadDependency(@NotNull Dependency dependency);

    /**
     * Adds the dependency location to the class loader search
     *
     * @param depend The {@link URL} to the place of the dependency
     */
    public abstract void addDependency(@NotNull URL depend);
}
