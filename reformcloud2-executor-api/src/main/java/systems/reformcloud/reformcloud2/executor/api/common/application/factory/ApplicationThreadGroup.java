package systems.reformcloud.reformcloud2.executor.api.common.application.factory;

import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.application.LoadedApplication;

public final class ApplicationThreadGroup extends ThreadGroup {

  public ApplicationThreadGroup(@Nonnull LoadedApplication application) {
    super(application.getName());
  }
}
