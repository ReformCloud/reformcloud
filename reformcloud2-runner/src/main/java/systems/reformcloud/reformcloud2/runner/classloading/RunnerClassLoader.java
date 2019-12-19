package systems.reformcloud.reformcloud2.runner.classloading;

import java.net.URL;
import java.net.URLClassLoader;

public final class RunnerClassLoader extends URLClassLoader {

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public RunnerClassLoader(ClassLoader loader) {
        super(new URL[0], loader);
    }

    public RunnerClassLoader(URL[] urls) {
        super(urls, ClassLoader.getSystemClassLoader());
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
}
