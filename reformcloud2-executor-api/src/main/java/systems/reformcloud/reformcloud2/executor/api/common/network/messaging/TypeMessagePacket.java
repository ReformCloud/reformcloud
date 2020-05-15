/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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

    private Collection<ReceiverType> receivers;
    private JsonConfiguration content;
    private String baseChannel;
    private String subChannel;

    public TypeMessagePacket() {
    }

    public TypeMessagePacket(Collection<ReceiverType> receivers, JsonConfiguration content, String baseChannel, String subChannel) {
        this.receivers = receivers;
        this.content = content;
        this.baseChannel = baseChannel;
        this.subChannel = subChannel;
    }

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
