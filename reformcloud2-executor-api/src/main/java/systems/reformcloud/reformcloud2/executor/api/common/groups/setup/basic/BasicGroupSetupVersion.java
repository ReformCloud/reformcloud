package systems.reformcloud.reformcloud2.executor.api.common.groups.setup.basic;

import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.setup.GroupSetupVersion;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

public class BasicGroupSetupVersion implements GroupSetupVersion {

    public BasicGroupSetupVersion(@Nonnull String name, @Nonnull ProcessGroup[] groups, @Nonnull MainGroup[] mainGroups) {
        this.name = name;
        this.groups = Arrays.asList(groups);
        this.mainGroups = Arrays.asList(mainGroups);
    }

    private final Collection<ProcessGroup> groups;

    private final Collection<MainGroup> mainGroups;

    private final String name;

    @Override
    public void install(@Nonnull Consumer<ProcessGroup> processGroupInstaller, @Nonnull Consumer<MainGroup> mainGroupInstaller) {
        this.mainGroups.forEach(mainGroupInstaller);
        this.groups.forEach(processGroupInstaller);
    }

    @Nonnull
    @Override
    public String getName() {
        return this.name;
    }
}
