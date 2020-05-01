package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

public abstract class PacketAPIProcessCopy implements Packet {

    public PacketAPIProcessCopy() {
    }

    public PacketAPIProcessCopy(String targetTemplate, String targetTemplateStorage, String targetTemplateGroup) {
        this.targetTemplate = targetTemplate;
        this.targetTemplateStorage = targetTemplateStorage;
        this.targetTemplateGroup = targetTemplateGroup;
    }

    protected String targetTemplate;

    protected String targetTemplateStorage;

    protected String targetTemplateGroup;

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.targetTemplate);
        buffer.writeString(this.targetTemplateStorage);
        buffer.writeString(this.targetTemplateGroup);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.targetTemplate = buffer.readString();
        this.targetTemplateStorage = buffer.readString();
        this.targetTemplateGroup = buffer.readString();
    }
}
