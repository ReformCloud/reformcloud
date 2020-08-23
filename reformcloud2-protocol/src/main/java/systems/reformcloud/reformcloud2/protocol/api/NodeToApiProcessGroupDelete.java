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
package systems.reformcloud.reformcloud2.protocol.api;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.event.events.group.ProcessGroupDeleteEvent;
import systems.reformcloud.reformcloud2.executor.api.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.network.channel.EndpointChannelReader;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.protocol.ProtocolPacket;

public class NodeToApiProcessGroupDelete extends ProtocolPacket {

    public NodeToApiProcessGroupDelete() {
    }

    public NodeToApiProcessGroupDelete(ProcessGroup processGroup) {
        this.processGroup = processGroup;
    }

    private ProcessGroup processGroup;

    @Override
    public int getId() {
        return NetworkUtil.API_BUS + 6;
    }

    @Override
    public void handlePacketReceive(@NotNull EndpointChannelReader reader, @NotNull NetworkChannel channel) {
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class)
                .callEvent(new ProcessGroupDeleteEvent(this.processGroup));
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObject(this.processGroup);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.processGroup = buffer.readObject(ProcessGroup.class);
    }
}
