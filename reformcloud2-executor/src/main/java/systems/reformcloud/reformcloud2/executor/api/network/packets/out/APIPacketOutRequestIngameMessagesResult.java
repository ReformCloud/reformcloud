package systems.reformcloud.reformcloud2.executor.api.network.packets.out;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.groups.messages.IngameMessages;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryResultPacket;

public class APIPacketOutRequestIngameMessagesResult extends QueryResultPacket {

    public APIPacketOutRequestIngameMessagesResult() {
    }

    public APIPacketOutRequestIngameMessagesResult(IngameMessages ingameMessages) {
        this.ingameMessages = ingameMessages;
    }

    private IngameMessages ingameMessages;

    public IngameMessages getIngameMessages() {
        return ingameMessages;
    }

    @Override
    public int getId() {
        return NetworkUtil.CONTROLLER_QUERY_BUS + 1;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObject(this.ingameMessages);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.ingameMessages = buffer.readObject(IngameMessages.class);
    }
}
