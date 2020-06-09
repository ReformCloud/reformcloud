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
package systems.refomcloud.reformcloud2.embedded.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.network.channel.manager.ChannelManager;
import systems.reformcloud.reformcloud2.executor.api.network.channel.shared.SharedEndpointChannelReader;
import systems.reformcloud.reformcloud2.executor.api.network.packet.Packet;
import systems.reformcloud.reformcloud2.protocol.shared.PacketAuthBegin;
import systems.reformcloud.reformcloud2.protocol.shared.PacketAuthSuccess;

public class EmbeddedEndpointChannelReader extends SharedEndpointChannelReader {

    public EmbeddedEndpointChannelReader() {
        PacketRegister.preAuth();
    }

    private boolean wasActive = false;

    @Override
    public boolean shouldHandle(@NotNull Packet packet) {
        return super.networkChannel.isAuthenticated() || packet.getId() == NetworkUtil.AUTH_BUS_END;
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext context) {
        synchronized (this) {
            if (!this.wasActive) {
                this.wasActive = true;
            } else {
                return;
            }
        }

        context.writeAndFlush(new PacketAuthBegin(
                Embedded.getInstance().getConfig().getConnectionKey(),
                2,
                new JsonConfiguration().add("pid", Embedded.getInstance().getCurrentProcessInformation().getProcessDetail().getProcessUniqueID())
        ), context.voidPromise());
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext context) {
        Channel channel = context.channel();
        if (channel.isOpen() && channel.isWritable()) {
            return;
        }

        super.networkChannel.close();
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class).unregisterChannel(super.networkChannel);
        System.exit(0);
    }

    @Override
    public void read(@NotNull Packet input) {
        if (input.getId() == NetworkUtil.AUTH_BUS_END) {
            if (!(input instanceof PacketAuthSuccess)) {
                // should never happen
                super.networkChannel.close();
                return;
            }

            super.networkChannel.setName("Controller");
            super.networkChannel.setAuthenticated(true);
            ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class).registerChannel(super.networkChannel);
            PacketRegister.postAuth();
            return;
        }

        super.read(input);
    }
}
