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
package systems.reformcloud.reformcloud2.node.process;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.channel.manager.ChannelManager;
import systems.reformcloud.reformcloud2.executor.api.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.network.packet.query.QueryManager;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.wrappers.ProcessWrapper;
import systems.reformcloud.reformcloud2.node.protocol.NodeToNodeCopyProcessToTemplate;
import systems.reformcloud.reformcloud2.node.protocol.NodeToNodeGetLastLogLines;
import systems.reformcloud.reformcloud2.node.protocol.NodeToNodeGetLastLogLinesResult;
import systems.reformcloud.reformcloud2.node.protocol.NodeToNodeRequestProcessUpdate;
import systems.reformcloud.reformcloud2.node.protocol.NodeToNodeRequestProcessUpdateResult;
import systems.reformcloud.reformcloud2.node.protocol.NodeToNodeSendProcessCommand;
import systems.reformcloud.reformcloud2.node.protocol.NodeToNodeSetProcessRuntimeState;
import systems.reformcloud.reformcloud2.node.protocol.NodeToNodeUploadProcessLog;
import systems.reformcloud.reformcloud2.node.protocol.NodeToNodeUploadProcessLogResult;
import systems.reformcloud.reformcloud2.shared.Constants;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class DefaultNodeRemoteProcessWrapper implements ProcessWrapper {

    protected ProcessInformation processInformation;

    public DefaultNodeRemoteProcessWrapper(ProcessInformation processInformation) {
        this.processInformation = processInformation;
    }

    @NotNull
    @Override
    public ProcessInformation getProcessInformation() {
        return this.processInformation;
    }

    protected void setProcessInformation(@NotNull ProcessInformation information) {
        this.processInformation = information;
    }

    @NotNull
    @Override
    public Optional<ProcessInformation> requestProcessInformationUpdate() {
        Packet result = this.sendQueryToProcessNode(new NodeToNodeRequestProcessUpdate(this.processInformation.getProcessDetail().getProcessUniqueID()));
        if (!(result instanceof NodeToNodeRequestProcessUpdateResult)) {
            return Optional.empty();
        }

        return Optional.ofNullable(((NodeToNodeRequestProcessUpdateResult) result).getProcessInformation());
    }

    @NotNull
    @Override
    public Optional<String> uploadLog() {
        Packet result = this.sendQueryToProcessNode(new NodeToNodeUploadProcessLog(this.processInformation.getProcessDetail().getProcessUniqueID()));
        if (!(result instanceof NodeToNodeUploadProcessLogResult)) {
            return Optional.empty();
        }

        return Optional.ofNullable(((NodeToNodeUploadProcessLogResult) result).getLogUrl());
    }

    @NotNull
    @Override
    public @UnmodifiableView Queue<String> getLastLogLines() {
        Packet result = this.sendQueryToProcessNode(new NodeToNodeGetLastLogLines(this.processInformation.getProcessDetail().getProcessUniqueID()));
        if (!(result instanceof NodeToNodeGetLastLogLinesResult)) {
            return Constants.EMPTY_STRING_QUEUE;
        }

        return ((NodeToNodeGetLastLogLinesResult) result).getLastLogLines();
    }

    @Override
    public void sendCommand(@NotNull String commandLine) {
        this.sendPacketToProcessNode(new NodeToNodeSendProcessCommand(this.processInformation.getProcessDetail().getProcessUniqueID(), commandLine));
    }

    @Override
    public void setRuntimeState(@NotNull ProcessState state) {
        this.sendPacketToProcessNode(new NodeToNodeSetProcessRuntimeState(this.processInformation.getProcessDetail().getProcessUniqueID(), state));
    }

    @Override
    public void copy(@NotNull String templateGroup, @NotNull String templateName, @NotNull String templateBackend) {
        this.sendPacketToProcessNode(new NodeToNodeCopyProcessToTemplate(
            this.processInformation.getProcessDetail().getProcessUniqueID(), templateGroup, templateName, templateBackend
        ));
    }

    private void sendPacketToProcessNode(@NotNull Packet packet) {
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class).getChannel(
            this.processInformation.getProcessDetail().getParentName()
        ).ifPresent(channel -> channel.sendPacket(packet));
    }

    private @Nullable Packet sendQueryToProcessNode(@NotNull Packet packet) {
        NetworkChannel networkChannel = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class).getChannel(
            this.processInformation.getProcessDetail().getParentName()
        ).orElse(null);
        if (networkChannel == null) {
            return null;
        }

        return ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(QueryManager.class).sendPacketQuery(
            networkChannel,
            packet
        ).getUninterruptedly(TimeUnit.SECONDS, 5);
    }
}
