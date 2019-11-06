package systems.reformcloud.reformcloud2.executor.api.common.application;

import systems.reformcloud.reformcloud2.executor.api.common.dependency.Dependency;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.jar.JarEntry;

public interface ApplicationConfig extends Nameable {

    @Nonnull
    String version();

    @Nonnull
    String author();

    @Nonnull
    String main();

    @Nonnull
    Dependency[] dependencies();

    @Nonnull
    String description();

    @Nonnull
    String website();

    @Nonnull
    String implementedVersion();

    @Nonnull
    File applicationFile();

    @Nonnull
    JarEntry applicationConfigFile();
}
