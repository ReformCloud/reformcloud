package systems.reformcloud.reformcloud2.executor.api.common.application;

import com.google.gson.reflect.TypeToken;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.application.basic.DefaultInstallableApplication;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

/**
 * Represents an application which can get installed
 */
public interface InstallableApplication extends Nameable {

  TypeToken<DefaultInstallableApplication> TYPE =
      new TypeToken<DefaultInstallableApplication>() {};

  /**
   * @return The download url of the application
   */
  @Nonnull String url();

  /**
   * @return The application which loader which should load the application
   * @deprecated This feature is not implemented and may get removed in a
   *     further release
   */
  @Nullable @Deprecated ApplicationLoader loader();
}
