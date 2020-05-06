package systems.reformcloud.reformcloud2.signs.application.packets;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.signs.application.ReformCloudApplication;
import systems.reformcloud.reformcloud2.signs.packets.PacketUtil;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

import java.util.Collection;

public class PacketDeleteBulkSigns extends Packet {

    public PacketDeleteBulkSigns() {
    }

    public PacketDeleteBulkSigns(Collection<CloudSign> cloudSigns) {
        this.cloudSigns = cloudSigns;
    }

    private Collection<CloudSign> cloudSigns;

    @Override
    public int getId() {
        return PacketUtil.SIGN_BUS + 7;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        for (CloudSign cloudSign : cloudSigns) {
            ReformCloudApplication.delete(cloudSign);
            DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new PacketDeleteSign(cloudSign)));
        }
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObjects(this.cloudSigns);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.cloudSigns = buffer.readObjects(CloudSign.class);
    }
}
