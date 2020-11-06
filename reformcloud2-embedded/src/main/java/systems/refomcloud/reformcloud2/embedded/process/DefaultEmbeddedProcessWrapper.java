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
package systems.refomcloud.reformcloud2.embedded.process;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.wrappers.ProcessWrapper;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeCopyProcess;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetLastProcessLogLines;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetProcessInformationResult;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetStringCollectionResult;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeRequestProcessInformationUpdate;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeSendProcessCommand;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeSetProcessRuntimeState;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeUploadProcessLog;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeUploadProcessLogResult;
import systems.reformcloud.reformcloud2.shared.Constants;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

public class DefaultEmbeddedProcessWrapper implements ProcessWrapper {

    private ProcessInformation processInformation;

    public DefaultEmbeddedProcessWrapper(ProcessInformation processInformation) {
        this.processInformation = processInformation;
    }

    @NotNull
    @Override
    public ProcessInformation getProcessInformation() {
        return this.processInformation;
    }

    @NotNull
    @Override
    public Optional<ProcessInformation> requestProcessInformationUpdate() {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeRequestProcessInformationUpdate(this.processInformation))
            .map(result -> {
                if (result instanceof ApiToNodeGetProcessInformationResult) {
                    ProcessInformation process = ((ApiToNodeGetProcessInformationResult) result).getProcessInformation();
                    return process != null ? this.processInformation = process : null;
                }

                return null;
            });
    }

    @NotNull
    @Override
    public Optional<String> uploadLog() {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeUploadProcessLog(this.processInformation))
            .map(result -> {
                if (result instanceof ApiToNodeUploadProcessLogResult) {
                    return ((ApiToNodeUploadProcessLogResult) result).getLogUrl();
                }

                return null;
            });
    }

    @NotNull
    @Override
    public @UnmodifiableView Queue<String> getLastLogLines() {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetLastProcessLogLines(this.processInformation))
            .map(result -> {
                if (result instanceof ApiToNodeGetStringCollectionResult) {
                    return new ArrayDeque<>(((ApiToNodeGetStringCollectionResult) result).getResult());
                }

                return new ArrayDeque<String>();
            }).orElse(Constants.EMPTY_STRING_QUEUE);
    }

    @Override
    public void sendCommand(@NotNull String commandLine) {
        Embedded.getInstance().sendPacket(new ApiToNodeSendProcessCommand(this.processInformation, commandLine));
    }

    @Override
    public void setRuntimeState(@NotNull ProcessState state) {
        Embedded.getInstance().sendPacket(new ApiToNodeSetProcessRuntimeState(this.processInformation, state));
    }

    @Override
    public void copy(@NotNull String templateGroup, @NotNull String templateName, @NotNull String templateBackend) {
        Embedded.getInstance().sendPacket(new ApiToNodeCopyProcess(this.processInformation, templateGroup, templateName, templateBackend));
    }
}
