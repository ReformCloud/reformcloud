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
package systems.reformcloud.reformcloud2.node.process;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.wrappers.ProcessWrapper;
import systems.reformcloud.reformcloud2.node.cluster.ClusterManager;
import systems.reformcloud.reformcloud2.shared.process.AbstractProcessBuilder;

final class DefaultNodeProcessBuilder extends AbstractProcessBuilder {

    @NotNull
    @Override
    public Task<ProcessWrapper> prepare() {
        if (super.processGroupName != null && super.processGroup == null) {
            super.processGroup = ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroup(super.processGroupName).orElse(null);
        }

        Conditions.nonNull(super.processGroup, "Unable to create process with no group defined to prepare from");
        return ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class).createProcess(
                super.processGroup, super.node, super.displayName, super.messageOfTheDay, super.template, super.inclusions,
                super.extra, super.initialState, super.processUniqueId, super.memory, super.id, super.maxPlayers, super.targetProcessFactory
        );
    }
}
