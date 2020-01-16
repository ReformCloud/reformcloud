package systems.reformcloud.reformcloud2.executor.api.common.groups.setup;

import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public interface GroupSetupVersion extends Nameable {

    void install(@Nonnull Consumer<ProcessGroup> processGroupInstaller, @Nonnull Consumer<MainGroup> mainGroupInstaller);
}
