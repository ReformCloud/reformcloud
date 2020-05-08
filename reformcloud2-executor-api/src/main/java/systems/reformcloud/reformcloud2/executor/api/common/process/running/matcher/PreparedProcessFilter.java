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
package systems.reformcloud.reformcloud2.executor.api.common.process.running.matcher;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessInclusion;

import java.util.Collection;

public final class PreparedProcessFilter {

    private PreparedProcessFilter() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    public static ProcessInformation findMayMatchingProcess(@NotNull ProcessConfiguration configuration,
                                                            @NotNull Collection<ProcessInformation> prepared) {
        ProcessInformation maybe = null;

        for (ProcessInformation information : prepared) {
            if (information.getProcessDetail().getProcessUniqueID().equals(configuration.getUniqueId()) || isEqual(information, configuration)) {
                if (maybe == null) {
                    maybe = information;
                } else if (maybe.getProcessDetail().getId() > information.getProcessDetail().getId()) {
                    maybe = information;
                }
            }
        }

        return maybe;
    }

    private static boolean isEqual(@NotNull ProcessInformation information, @NotNull ProcessConfiguration configuration) {
        return (configuration.getPort() == null || configuration.getPort() == information.getNetworkInfo().getPort())
                && (configuration.getId() == -1 || configuration.getId() == information.getProcessDetail().getId())
                && (configuration.getTemplate() == null
                || configuration.getTemplate().getName().equals(information.getProcessDetail().getTemplate().getName()))
                && (configuration.getMaxMemory() == null || configuration.getMaxMemory().equals(information.getProcessDetail().getMaxMemory()))
                && configuration.getExtra().toPrettyString().equals(information.getExtra().toPrettyString())
                && (configuration.getDisplayName() == null || information.getProcessDetail().getDisplayName().equals(configuration.getDisplayName()))
                && matchesAllInclusions(information, configuration);
    }

    private static boolean matchesAllInclusions(@NotNull ProcessInformation processInformation,
                                                @NotNull ProcessConfiguration configuration) {
        if (processInformation.getPreInclusions().size() != configuration.getInclusions().size()) {
            return false;
        }

        for (ProcessInclusion inclusion : configuration.getInclusions()) {
            if (processInformation.getPreInclusions().stream().noneMatch(
                    e -> e.getName().equals(inclusion.getName()) && e.getUrl().equals(inclusion.getUrl())
            )) {
                return false;
            }
        }

        return true;
    }
}
