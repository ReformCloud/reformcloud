package systems.reformcloud.reformcloud2.executor.api.common.application.factory;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.application.LoadedApplication;

public final class ApplicationThreadGroup extends ThreadGroup {

    public ApplicationThreadGroup(@NotNull LoadedApplication application) {
        super(application.getName());
    }
}
