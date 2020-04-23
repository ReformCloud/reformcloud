package systems.reformcloud.reformcloud2.executor.api.common.plugins.basic;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin;

import java.util.List;

public final class DefaultPlugin extends Plugin {

    @ApiStatus.Internal
    public DefaultPlugin() {
    }

    public DefaultPlugin(String version, String author, String main, List<String> depends, List<String> softpends, boolean enabled, String name) {
        this.version = version;
        this.author = author;
        this.main = main;
        this.depends = depends;
        this.softpends = softpends;
        this.enabled = enabled;
        this.name = name;
    }

    private String version;

    private String author;

    private String main;

    private List<String> depends;

    private List<String> softpends;

    private boolean enabled;

    private String name;

    @NotNull
    @Override
    public String version() {
        return version;
    }

    @Nullable
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
        return depends;
    }

    @Override
    public List<String> softpends() {
        return softpends;
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.version);
        buffer.writeString(this.author);
        buffer.writeString(this.main);
        buffer.writeStringArray(this.depends);
        buffer.writeStringArray(this.softpends);
        buffer.writeBoolean(this.enabled);
        buffer.writeString(this.name);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.version = buffer.readString();
        this.author = buffer.readString();
        this.main = buffer.readString();
        this.depends = buffer.readStringArray();
        this.softpends = buffer.readStringArray();
        this.enabled = buffer.readBoolean();
        this.name = buffer.readString();
    }
}
