package systems.reformcloud.reformcloud2.executor.api.common.application;

/**
 * Represents the lifecycle of an application
 */
public enum ApplicationStatus {

  /**
   * The application is ready to get installed
   */
  INSTALLABLE,

  /**
   * The application is installed
   */
  INSTALLED,

  /**
   * The application is loaded
   */
  LOADED,

  /**
   * The application is enabled
   */
  ENABLED,

  /**
   * The application is ready to get disabled
   */
  PRE_DISABLE,

  /**
   * The application is disabled
   */
  DISABLED,

  /**
   * The application is uninstalling
   */
  UNINSTALLING,

  /**
   * The application is completely removed from the runtime
   */
  UNINSTALLED
}
