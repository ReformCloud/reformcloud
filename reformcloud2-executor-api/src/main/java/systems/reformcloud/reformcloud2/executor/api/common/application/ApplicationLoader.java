package systems.reformcloud.reformcloud2.executor.api.common.application;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a loader for applications
 */
public interface ApplicationLoader {

  /**
   * Detects all applications
   */
  void detectApplications();

  /**
   * Installs all applications
   */
  void installApplications();

  /**
   * Loads all applications
   */
  void loadApplications();

  /**
   * Enables all applications
   */
  void enableApplications();

  /**
   * Disables all applications
   */
  void disableApplications();

  /**
   * Installs an specific application
   *
   * @param application The application which should get installed
   * @return If the cloud can find the application and install it {@code true}
   *     else {@code false}
   */
  boolean
  doSpecificApplicationInstall(@Nonnull InstallableApplication application);

  /**
   * Unloads a specific aplpication
   *
   * @param loadedApplication The application which should get unloaded
   * @return If the application was loaded and got unloaded
   */
  boolean
  doSpecificApplicationUninstall(@Nonnull LoadedApplication loadedApplication);

  /**
   * Finds the {@link LoadedApplication} by the name and unloads it
   *
   * @see #getApplication(String)
   * @see #doSpecificApplicationUninstall(LoadedApplication)
   *
   * @param application The name oof the application which should get unloaded
   * @return If the application was loaded and got unloaded
   */
  boolean doSpecificApplicationUninstall(@Nonnull String application);

  /**
   * Get a specific application
   *
   * @param name The name of the application
   * @return The loaded application or {@code null} if the application is
   *     unknown
   */
  @Nullable LoadedApplication getApplication(@Nonnull String name);

  /**
   * The name of a loaded application
   *
   * @see LoadedApplication#getName()
   * @param loadedApplication The application from which the name is needed
   * @return The name of the application
   */
  @Nonnull
  String getApplicationName(@Nonnull LoadedApplication loadedApplication);

  /**
   * @return All currently loaded applications in the runtime
   */
  @Nonnull List<LoadedApplication> getApplications();

  /**
   * Registers an {@link ApplicationHandler}
   *
   * @param applicationHandler The handler which should get registered
   */
  void addApplicationHandler(@Nonnull ApplicationHandler applicationHandler);
}
