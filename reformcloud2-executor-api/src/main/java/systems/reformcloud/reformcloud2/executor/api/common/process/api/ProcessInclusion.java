package systems.reformcloud.reformcloud2.executor.api.common.process.api;

import javax.annotation.Nonnull;

public class ProcessInclusion {

    public ProcessInclusion(@Nonnull String url, @Nonnull String name) {
        this.url = url;
        this.name = name;
    }

    private final String url;

    private final String name;

    @Nonnull
    public String getUrl() {
        return url;
    }

    @Nonnull
    public String getName() {
        return name;
    }
}
