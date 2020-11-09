/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.reformcloud2.signs.application.packets;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.network.channel.listener.ChannelListener;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.channel.manager.ChannelManager;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.network.packet.Packet;
import systems.reformcloud.reformcloud2.signs.application.ReformCloudApplication;
import systems.reformcloud.reformcloud2.signs.packets.PacketUtil;
import systems.reformcloud.reformcloud2.signs.util.SignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

public class PacketCreateSign extends Packet {

    private CloudSign cloudSign;

    public PacketCreateSign() {
    }

    public PacketCreateSign(CloudSign cloudSign) {
        this.cloudSign = cloudSign;
    }

    @Override
    public int getId() {
        return PacketUtil.SIGN_BUS + 2;
    }

    @Override
    public void handlePacketReceive(@NotNull ChannelListener reader, @NotNull NetworkChannel channel) {
        if (ExecutorAPI.getInstance().getType() != ExecutorType.API) {
            ReformCloudApplication.insert(this.cloudSign);
            for (NetworkChannel registeredChannel : ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class).getRegisteredChannels()) {
                if (registeredChannel.isAuthenticated()) {
                    registeredChannel.sendPacket(new PacketCreateSign(this.cloudSign));
                }
            }
        } else {
            SignSystemAdapter.getInstance().handleInternalSignCreate(this.cloudSign);
        }
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObject(this.cloudSign);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.cloudSign = buffer.readObject(CloudSign.class);
    }
}
