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
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.reformcloud.reformcloud2.executor.api.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.wrappers.ProcessWrapper;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetProcessInformationResult;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodePrepareProcess;
import systems.reformcloud.reformcloud2.shared.process.AbstractProcessBuilder;

public class DefaultEmbeddedProcessBuilder extends AbstractProcessBuilder {

    @NotNull
    @Override
    public Task<ProcessWrapper> prepare() {
        Conditions.isTrue(super.processGroupName != null || super.processGroup != null, "No group name or group given to prepare from");
        return Task.supply(() -> {
            Packet packet = Embedded.getInstance().sendSyncQuery(new ApiToNodePrepareProcess(
                super.processGroupName, super.node, super.displayName, super.messageOfTheDay, super.targetProcessFactory,
                super.processGroup, super.template, super.inclusions, super.extra, super.initialState,
                super.processUniqueId, super.memory, super.id, super.maxPlayers
            )).orElse(null);
            if (packet instanceof ApiToNodeGetProcessInformationResult) {
                return new DefaultEmbeddedProcessWrapper(((ApiToNodeGetProcessInformationResult) packet).getProcessInformation());
            } else {
                return null;
            }
        });
    }
}
