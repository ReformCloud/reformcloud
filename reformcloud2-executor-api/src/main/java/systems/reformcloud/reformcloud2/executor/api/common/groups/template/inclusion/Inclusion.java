package systems.reformcloud.reformcloud2.executor.api.common.groups.template.inclusion;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

public class Inclusion implements SerializableObject {

    @ApiStatus.Internal
    public Inclusion() {
    }

    public Inclusion(String key, String backend, InclusionLoadType inclusionLoadType) {
        this.key = key;
        this.backend = backend;
        this.inclusionLoadType = inclusionLoadType;
    }

    private String key;

    private String backend;

    private InclusionLoadType inclusionLoadType;

    public String getKey() {
        return key;
    }

    public String getBackend() {
        return backend;
    }

    public InclusionLoadType getInclusionLoadType() {
        return inclusionLoadType;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.key);
        buffer.writeString(this.backend);
        buffer.writeVarInt(this.inclusionLoadType.ordinal());
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.key = buffer.readString();
        this.backend = buffer.readString();
        this.inclusionLoadType = InclusionLoadType.values()[buffer.readVarInt()];
    }

    public enum InclusionLoadType {

        PRE,

        PAST

    }
}
