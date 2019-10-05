package systems.reformcloud.reformcloud2.runner.classloading;

import java.net.URL;
import java.net.URLClassLoader;

public final class RunnerClassLoader extends URLClassLoader {

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public RunnerClassLoader(URL[] urls) {
        super(urls, ClassLoader.getSystemClassLoader());
    }
}
