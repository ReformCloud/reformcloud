package systems.reformcloud.reformcloud2.executor.api.common.application;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.Dependency;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import java.io.File;
import java.util.jar.JarEntry;

/**
 * Represents a config of an application
 *
 * @see LoadedApplication#applicationConfig()
 */
public interface ApplicationConfig extends Nameable {

    /**
     * @return The version of the application
     */
    @NotNull
    String version();

    /**
     * @return The author of the application
     */
    @NotNull
    String author();

    /**
     * @return The main class of the application
     */
    @NotNull
    String main();

    /**
     * @return All needed dependencies for an application
     */
    @NotNull
    Dependency[] dependencies();

    /**
     * @return The description of the application
     */
    @NotNull
    String description();

    /**
     * @return The website of an application
     */
    @NotNull
    String website();

    /**
     * @return The api version which the application is using
     */
    @NotNull
    String implementedVersion();

    /**
     * @return The file from which the application is loaded
     */
    @NotNull
    File applicationFile();

    /**
     * @return The {@link JarEntry} from which the application config is loaded
     */
    @NotNull
    JarEntry applicationConfigFile();
}
