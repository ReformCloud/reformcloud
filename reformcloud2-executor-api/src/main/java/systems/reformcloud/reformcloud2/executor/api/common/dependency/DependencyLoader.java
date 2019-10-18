package systems.reformcloud.reformcloud2.executor.api.common.dependency;

import systems.reformcloud.reformcloud2.runner.classloading.RunnerClassLoader;

import java.net.URL;

public abstract class DependencyLoader {

    public static void doLoad() {
        DependencyLoader dependencyLoader = new DefaultDependencyLoader();
        dependencyLoader.loadDependencies();
        dependencyLoader.addDependencies();
    }

    void addURL(URL url) {
        RunnerClassLoader urlClassLoader = (RunnerClassLoader) Thread.currentThread().getContextClassLoader();
        urlClassLoader.addURL(url);
    }

    public abstract void loadDependencies();

    public abstract void addDependencies();

    public abstract URL loadDependency(Dependency dependency);

    public abstract void addDependency(URL depend);
}
