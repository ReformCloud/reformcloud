package systems.reformcloud.reformcloud2.executor.api.common.process.running.matcher;

import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public final class PreparedProcessFilter {

    private PreparedProcessFilter() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    public static ProcessInformation findMayMatchingProcess(@Nonnull ProcessConfiguration configuration,
                                                            @Nonnull Collection<ProcessInformation> prepared) {
        for (ProcessInformation information : prepared) {
            if (information.getProcessUniqueID().equals(configuration.getUniqueId()) || isEqual(information, configuration)) {
                return information;
            }
        }

        return null;
    }

    private static boolean isEqual(@Nonnull ProcessInformation information, @Nonnull ProcessConfiguration configuration) {
        return (configuration.getPort() == null || configuration.getPort() == information.getNetworkInfo().getPort())
                && (configuration.getId() == -1 || configuration.getId() == information.getId())
                && (configuration.getTemplate() == null
                || configuration.getTemplate().getName().equals(information.getTemplate().getName()))
                && (configuration.getMaxMemory() == null || configuration.getMaxMemory().equals(information.getMaxMemory()))
                && configuration.getExtra().toPrettyString().equals(information.getExtra().toPrettyString())
                && (configuration.getDisplayName() == null || information.getDisplayName().equals(configuration.getDisplayName()));
    }
}
