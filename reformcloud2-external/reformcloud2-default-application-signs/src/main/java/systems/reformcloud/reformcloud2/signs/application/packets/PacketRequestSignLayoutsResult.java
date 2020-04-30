package systems.reformcloud.reformcloud2.signs.application.packets;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryResultPacket;
import systems.reformcloud.reformcloud2.signs.packets.PacketUtil;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;

public class PacketRequestSignLayoutsResult extends QueryResultPacket {

    public PacketRequestSignLayoutsResult() {
    }

    public PacketRequestSignLayoutsResult(SignConfig signConfig) {
        this.signConfig = signConfig;
    }

    private SignConfig signConfig;

    public SignConfig getSignConfig() {
        return signConfig;
    }

    @Override
    public int getId() {
        return PacketUtil.SIGN_BUS + 8;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObject(this.signConfig);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.signConfig = buffer.readObject(SignConfig.class);
    }
}
