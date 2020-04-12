package systems.reformcloud.reformcloud2.executor.api.common.plugins.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.InstallablePlugin;

import java.util.ArrayList;
import java.util.List;

public final class DefaultInstallablePlugin extends InstallablePlugin {

    public DefaultInstallablePlugin(String downloadURL, String name, String version, String author, String main) {
        this.downloadURL = downloadURL;
        this.name = name;
        this.version = version;
        this.author = author;
        this.main = main;
    }

    private final String downloadURL;

    private final String name;

    private final String version;

    private final String author;

    private final String main;

    @NotNull
    @Override
    public String getDownloadURL() {
        return downloadURL;
    }

    @NotNull
    @Override
    public String version() {
        return version;
    }

    @NotNull
    @Override
    public String author() {
        return author;
    }

    @NotNull
    @Override
    public String main() {
        return main;
    }

    @Override
    public List<String> depends() {
        return new ArrayList<>();
    }

    @Override
    public List<String> softpends() {
        return new ArrayList<>();
    }

    @Override
    public boolean enabled() {
        return false;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }
}
