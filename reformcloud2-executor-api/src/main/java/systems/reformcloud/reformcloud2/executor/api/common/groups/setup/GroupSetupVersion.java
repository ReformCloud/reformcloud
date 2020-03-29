package systems.reformcloud.reformcloud2.executor.api.common.groups.setup;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.function.Consumer;

public interface GroupSetupVersion extends Nameable {

    void install(@NotNull Consumer<ProcessGroup> processGroupInstaller, @NotNull Consumer<MainGroup> mainGroupInstaller);
}
