package systems.reformcloud.reformcloud2.runner.classloading;

import systems.reformcloud.reformcloud2.runner.util.ExceptionFunction;

import java.nio.file.Path;

public final class ClassPreparer {

    public static ClassLoader create(Path path, ExceptionFunction<Path, ClassLoader> function) {
        try {
            return function.apply(path);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
