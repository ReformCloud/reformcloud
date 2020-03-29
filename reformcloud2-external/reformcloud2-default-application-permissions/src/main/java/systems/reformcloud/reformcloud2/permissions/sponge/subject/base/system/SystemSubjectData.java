package systems.reformcloud.reformcloud2.permissions.sponge.subject.base.system;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectData;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.AbstractSpongeSubjectData;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class SystemSubjectData extends AbstractSpongeSubjectData {

    private static final Map<String, Boolean> PERMS = Collections.singletonMap("*", true);

    @Override
    @NotNull
    public Map<Set<Context>, Map<String, Boolean>> getAllPermissions() {
        return Collections.singletonMap(SubjectData.GLOBAL_CONTEXT, PERMS);
    }

    @Override
    @NotNull
    public Map<String, Boolean> getPermissions(@Nullable Set<Context> contexts) {
        return PERMS;
    }
}
