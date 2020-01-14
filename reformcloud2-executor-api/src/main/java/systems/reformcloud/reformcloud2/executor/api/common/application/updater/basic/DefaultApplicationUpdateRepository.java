package systems.reformcloud.reformcloud2.executor.api.common.application.updater.basic;

import systems.reformcloud.reformcloud2.executor.api.common.application.updater.ApplicationUpdateRepository;

import javax.annotation.Nonnull;

public abstract class DefaultApplicationUpdateRepository implements ApplicationUpdateRepository {

    @Nonnull
    @Override
    public String getName() {
        return "ReformCloud-Internal";
    }
}
