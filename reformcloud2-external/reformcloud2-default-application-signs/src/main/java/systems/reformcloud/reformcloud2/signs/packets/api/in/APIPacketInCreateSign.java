package systems.reformcloud.reformcloud2.signs.packets.api.in;

import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.signs.packets.PacketUtil;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class APIPacketInCreateSign implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return PacketUtil.SIGN_BUS + 4;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        CloudSign sign = packet.content().get("sign", CloudSign.TYPE);
        if (sign == null) {
            return;
        }

        SignSystemAdapter.getInstance().handleInternalSignCreate(sign);
    }
}
