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
package systems.reformcloud.reformcloud2.node.messaging;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.channel.manager.ChannelManager;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.provider.ChannelMessageProvider;
import systems.reformcloud.reformcloud2.node.NodeExecutor;
import systems.reformcloud.reformcloud2.node.protocol.NodeToNodePublishChannelMessage;
import systems.reformcloud.reformcloud2.protocol.shared.PacketChannelMessage;

public class DefaultNodeChannelMessageProvider implements ChannelMessageProvider {

    @Override
    public void sendChannelMessage(@NotNull ProcessInformation receiver, @NotNull String channel, @NotNull JsonConfiguration data) {
        this.getChannelManager().getChannel(receiver.getProcessDetail().getName()).ifPresent(netChannel -> netChannel.sendPacket(new PacketChannelMessage(channel, data)));
    }

    @Override
    public void sendChannelMessage(@NotNull String processGroup, @NotNull String channel, @NotNull JsonConfiguration data) {
        for (ProcessInformation processInformation : ExecutorAPI.getInstance().getProcessProvider().getProcessesByProcessGroup(processGroup)) {
            this.sendChannelMessage(processInformation, channel, data);
        }
    }

    @Override
    public void publishChannelMessageToAll(@NotNull String node, @NotNull String channel, @NotNull JsonConfiguration data) {
        if (NodeExecutor.getInstance().isOwnIdentity(node)) {
            ExecutorAPI.getInstance().getProcessProvider().getProcesses()
                    .stream()
                    .filter(processInformation -> processInformation.getProcessDetail().getParentName().equals(node))
                    .forEach(processInformation -> this.sendChannelMessage(processInformation, channel, data));
            return;
        }

        this.getChannelManager().getChannel(node).ifPresent(netChannel -> netChannel.sendPacket(new NodeToNodePublishChannelMessage(channel, data)));
    }

    @Override
    public void publishChannelMessage(@NotNull String channel, @NotNull JsonConfiguration data) {
        for (NetworkChannel registeredChannel : this.getChannelManager().getRegisteredChannels()) {
            registeredChannel.sendPacket(new PacketChannelMessage(channel, data));
        }
    }

    private @NotNull ChannelManager getChannelManager() {
        return ExecutorAPI.getInstance().getServiceRegistry().getProvider(ChannelManager.class).orElseThrow(() -> new RuntimeException("Channel manager was unregistered"));
    }
}
