package systems.reformcloud.reformcloud2.executor.api.common.network.messaging;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ErrorReportHandling;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

public class NamedMessagePacket implements Packet {

    public NamedMessagePacket() {
    }

    public NamedMessagePacket(Collection<String> receivers, JsonConfiguration content,
                              ErrorReportHandling handling, String baseChannel, String subChannel) {
        this.receivers = receivers;
        this.content = content;
        this.handling = handling;
        this.baseChannel = baseChannel;
        this.subChannel = subChannel;
    }

    private Collection<String> receivers;

    private JsonConfiguration content;

    private ErrorReportHandling handling;

    private String baseChannel;

    private String subChannel;

    @Override
    public int getId() {
        return NetworkUtil.MESSAGING_BUS + 2;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeVarInt(this.receivers.size());
        for (String receiver : receivers) {
            buffer.writeString(receiver);
        }

        buffer.writeArray(content.toPrettyBytes());
        buffer.writeVarInt(this.handling.ordinal());
        buffer.writeString(this.baseChannel);
        buffer.writeString(this.subChannel);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        int size = buffer.readVarInt();
        this.receivers = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            this.receivers.add(buffer.readString());
        }

        try (InputStream stream = new ByteArrayInputStream(buffer.readArray())) {
            this.content = new JsonConfiguration(stream);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        this.handling = ErrorReportHandling.values()[buffer.readVarInt()];
        this.baseChannel = buffer.readString();
        this.subChannel = buffer.readString();
    }
}
