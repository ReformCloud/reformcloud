package systems.reformcloud.reformcloud2.executor.api.common.process.api;

import org.jetbrains.annotations.NotNull;

public class ProcessInclusion {

    public ProcessInclusion(@NotNull String url, @NotNull String name) {
        this.url = url;
        this.name = name;
    }

    private final String url;

    private final String name;

    @NotNull
    public String getUrl() {
        return url;
    }

    @NotNull
    public String getName() {
        return name;
    }
}
