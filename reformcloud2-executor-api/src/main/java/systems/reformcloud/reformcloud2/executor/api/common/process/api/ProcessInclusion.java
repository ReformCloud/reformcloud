package systems.reformcloud.reformcloud2.executor.api.common.process.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

public class ProcessInclusion implements SerializableObject {

    @ApiStatus.Internal
    public ProcessInclusion() {
    }

    public ProcessInclusion(@NotNull String url, @NotNull String name) {
        this.url = url;
        this.name = name;
    }

    private String url;

    private String name;

    @NotNull
    public String getUrl() {
        return url;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.url);
        buffer.writeString(this.name);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.url = buffer.readString();
        this.name = buffer.readString();
    }
}
