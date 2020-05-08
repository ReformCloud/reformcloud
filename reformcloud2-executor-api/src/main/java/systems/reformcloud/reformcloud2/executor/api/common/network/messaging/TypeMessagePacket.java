package systems.reformcloud.reformcloud2.executor.api.common.network.messaging;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ReceiverType;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

public class TypeMessagePacket extends Packet {

    public TypeMessagePacket() {
    }

    public TypeMessagePacket(Collection<ReceiverType> receivers, JsonConfiguration content, String baseChannel, String subChannel) {
        this.receivers = receivers;
        this.content = content;
        this.baseChannel = baseChannel;
        this.subChannel = subChannel;
    }

    private Collection<ReceiverType> receivers;

    private JsonConfiguration content;

    private String baseChannel;

    private String subChannel;

    @Override
    public int getId() {
        return NetworkUtil.MESSAGING_BUS + 1;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        DefaultChannelManager.INSTANCE.broadcast(
                new ProxiedChannelMessage(this.content, this.baseChannel, this.subChannel),
                e -> {
                    ProcessInformation processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(e.getName());
                    if (this.receivers.contains(ReceiverType.OTHERS) && processInformation == null) {
                        return true;
                    }

                    if (processInformation == null) {
                        return false;
                    }

                    if (processInformation.getProcessDetail().getTemplate().isServer() && this.receivers.contains(ReceiverType.SERVER)) {
                        return true;
                    }

                    return !processInformation.getProcessDetail().getTemplate().isServer() && this.receivers.contains(ReceiverType.PROXY);
                }
        );
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeVarInt(this.receivers.size());
        for (ReceiverType receiver : this.receivers) {
            buffer.writeVarInt(receiver.ordinal());
        }

        buffer.writeArray(this.content.toPrettyBytes());
        buffer.writeString(this.baseChannel);
        buffer.writeString(this.subChannel);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        int size = buffer.readVarInt();
        this.receivers = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            receivers.add(ReceiverType.values()[buffer.readVarInt()]);
        }

        try (InputStream stream = new ByteArrayInputStream(buffer.readArray())) {
            this.content = new JsonConfiguration(stream);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        this.baseChannel = buffer.readString();
        this.subChannel = buffer.readString();
    }
}
