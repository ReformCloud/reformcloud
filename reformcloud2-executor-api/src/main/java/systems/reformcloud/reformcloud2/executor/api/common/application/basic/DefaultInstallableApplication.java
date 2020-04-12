package systems.reformcloud.reformcloud2.executor.api.common.application.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.application.InstallableApplication;

public final class DefaultInstallableApplication implements InstallableApplication {

    public DefaultInstallableApplication(String url, String name) {
        this.url = url;
        this.name = name;
    }

    private final String url;

    private final String name;

    @NotNull
    @Override
    public String url() {
        return url;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }
}
