package systems.reformcloud.reformcloud2.executor.api.common.application.basic;

import systems.reformcloud.reformcloud2.executor.api.common.application.InstallableApplication;

import javax.annotation.Nonnull;

public final class DefaultInstallableApplication implements InstallableApplication {

    public DefaultInstallableApplication(String url, String name) {
        this.url = url;
        this.name = name;
    }

    private final String url;

    private final String name;

    @Nonnull
    @Override
    public String url() {
        return url;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }
}
