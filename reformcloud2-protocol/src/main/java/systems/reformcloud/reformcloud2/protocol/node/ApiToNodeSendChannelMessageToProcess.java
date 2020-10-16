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
package systems.reformcloud.reformcloud2.protocol.node;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.network.channel.EndpointChannelReader;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.wrappers.ProcessWrapper;
import systems.reformcloud.reformcloud2.protocol.ProtocolPacket;

import java.util.Optional;
import java.util.UUID;

public class ApiToNodeSendChannelMessageToProcess extends ProtocolPacket {

    private UUID targetProcess;
    private String channel;
    private JsonConfiguration data;

    public ApiToNodeSendChannelMessageToProcess() {
    }

    public ApiToNodeSendChannelMessageToProcess(ProcessInformation targetProcess, String channel, JsonConfiguration data) {
        this.targetProcess = targetProcess.getProcessDetail().getProcessUniqueID();
        this.channel = channel;
        this.data = data;
    }

    @Override
    public int getId() {
        return NetworkUtil.EMBEDDED_BUS + 19;
    }

    @Override
    public void handlePacketReceive(@NotNull EndpointChannelReader reader, @NotNull NetworkChannel channel) {
        Optional<ProcessWrapper> process = ExecutorAPI.getInstance().getProcessProvider().getProcessByUniqueId(this.targetProcess);
        process.ifPresent(processWrapper -> ExecutorAPI.getInstance().getChannelMessageProvider().sendChannelMessage(
            processWrapper.getProcessInformation(),
            this.channel,
            this.data
        ));
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeUniqueId(this.targetProcess);
        buffer.writeString(this.channel);
        buffer.writeArray(this.data.toPrettyBytes());
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.targetProcess = buffer.readUniqueId();
        this.channel = buffer.readString();
        this.data = new JsonConfiguration(buffer.readArray());
    }
}
