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
package systems.reformcloud.reformcloud2.node.factory;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.groups.template.runtime.DefaultRuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.template.builder.DefaultTemplate;

final class MemoryCalculator {

    private MemoryCalculator() {
        throw new UnsupportedOperationException();
    }

    static int calcMemory(@NotNull String group, @NotNull DefaultTemplate template) {
        DefaultRuntimeConfiguration configuration = template.getRuntimeConfiguration();
        if (configuration.getMaxMemory() < 0 || configuration.getDynamicMemory() < 0) {
            return configuration.getMaxMemory() < 0 ? 512 : configuration.getMaxMemory();
        }

        if (configuration.getDynamicMemory() <= configuration.getMaxMemory()) {
            return configuration.getMaxMemory();
        }

        int online = ExecutorAPI.getInstance().getProcessProvider().getProcessesByProcessGroup(group).size();
        if (online > 9) {
            return configuration.getMaxMemory();
        }

        if (online == 0) {
            return configuration.getDynamicMemory();
        }

        return ((configuration.getDynamicMemory() - configuration.getMaxMemory()) / 100) * (((10 - online) * 10)) + configuration.getMaxMemory();
    }
}
