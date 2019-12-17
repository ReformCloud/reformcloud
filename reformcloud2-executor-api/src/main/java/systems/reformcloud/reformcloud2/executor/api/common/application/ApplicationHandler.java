package systems.reformcloud.reformcloud2.executor.api.common.application;

/**
 * Represents a handler for application actions in the runtime
 *
 * @see ApplicationLoader#addApplicationHandler(ApplicationHandler)
 */
public interface ApplicationHandler {

  /**
   * Gets called when the application in the app folder getting detected
   */
  void onDetectApplications();

  /**
   * Gets called when the applications are going to be installed
   */
  void onInstallApplications();

  /**
   * Gets called when the applications are going to be loaded
   */
  void onLoadApplications();

  /**
   * Gets called when the applications are going to be enabled
   */
  void onEnableApplications();

  /**
   * Gets called when the applications are going to be disabled
   */
  void onDisableApplications();
}
