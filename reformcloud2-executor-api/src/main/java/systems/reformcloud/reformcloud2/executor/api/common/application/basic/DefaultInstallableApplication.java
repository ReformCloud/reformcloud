package systems.reformcloud.reformcloud2.executor.api.common.application.basic;

import systems.reformcloud.reformcloud2.executor.api.common.application.ApplicationLoader;
import systems.reformcloud.reformcloud2.executor.api.common.application.InstallableApplication;

import javax.annotation.Nonnull;

public final class DefaultInstallableApplication implements InstallableApplication {

    public DefaultInstallableApplication(String url, ApplicationLoader loader, String name) {
        this.url = url;
        this.loader = loader;
        this.name = name;
    }

    private String url;

    private ApplicationLoader loader;

    private String name;

    @Nonnull
    @Override
    public String url() {
        return url;
    }

    @Override
    public ApplicationLoader loader() {
        return loader;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }
}
