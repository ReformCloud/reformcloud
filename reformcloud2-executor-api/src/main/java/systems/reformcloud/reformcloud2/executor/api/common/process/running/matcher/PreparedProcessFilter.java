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
        for (ProcessInformation information : prepared) {
            if (information.getProcessDetail().getProcessUniqueID().equals(configuration.getUniqueId()) || isEqual(information, configuration)) {
                return information;
            }
        }

        return null;
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
