package systems.reformcloud.reformcloud2.executor.api.common.plugins.basic;

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

    private String downloadURL;

    private String name;

    private String version;

    private String author;

    private String main;

    @Override
    public String getDownloadURL() {
        return downloadURL;
    }

    @Override
    public String version() {
        return version;
    }

    @Override
    public String author() {
        return author;
    }

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

    @Override
    public String getName() {
        return name;
    }
}
