package systems.reformcloud.reformcloud2.executor.api.common.application;

import java.io.File;
import java.util.jar.JarEntry;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.Dependency;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

/**
 * Represents a config of an application
 *
 * @see LoadedApplication#applicationConfig()
 */
public interface ApplicationConfig extends Nameable {

  /**
   * @return The version of the application
   */
  @Nonnull String version();

  /**
   * @return The author of the application
   */
  @Nonnull String author();

  /**
   * @return The main class of the application
   */
  @Nonnull String main();

  /**
   * @return All needed dependencies for an application
   */
  @Nonnull Dependency[] dependencies();

  /**
   * @return The description of the application
   */
  @Nonnull String description();

  /**
   * @return The website of an application
   */
  @Nonnull String website();

  /**
   * @return The api version which the application is using
   */
  @Nonnull String implementedVersion();

  /**
   * @return The file from which the application is loaded
   */
  @Nonnull File applicationFile();

  /**
   * @return The {@link JarEntry} from which the application config is loaded
   */
  @Nonnull JarEntry applicationConfigFile();
}
