package systems.reformcloud.reformcloud2.executor.controller.network.packet;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.client.basic.DefaultClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.controller.process.ClientManager;

public class ClientPacketNotifyRuntimeUpdate implements Packet {

    public ClientPacketNotifyRuntimeUpdate() {
    }

    public ClientPacketNotifyRuntimeUpdate(DefaultClientRuntimeInformation clientRuntimeInformation) {
        this.clientRuntimeInformation = clientRuntimeInformation;
    }

    private DefaultClientRuntimeInformation clientRuntimeInformation;

    @Override
    public int getId() {
        return NetworkUtil.CONTROLLER_INFORMATION_BUS + 4;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        ClientManager.INSTANCE.updateClient(this.clientRuntimeInformation);
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObject(this.clientRuntimeInformation);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.clientRuntimeInformation = buffer.readObject(DefaultClientRuntimeInformation.class);
    }
}
