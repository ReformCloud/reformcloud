package systems.reformcloud.reformcloud2.executor.api.common.application.updater.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.application.updater.ApplicationUpdateRepository;

public abstract class DefaultApplicationUpdateRepository implements ApplicationUpdateRepository {

    @NotNull
    @Override
    public String getName() {
        return "ReformCloud-Internal";
    }
}
