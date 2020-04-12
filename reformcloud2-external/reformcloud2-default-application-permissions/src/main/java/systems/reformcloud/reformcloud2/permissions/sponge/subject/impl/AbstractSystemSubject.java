package systems.reformcloud.reformcloud2.permissions.sponge.subject.impl;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.service.permission.SubjectData;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.AbstractSpongeSubject;

public abstract class AbstractSystemSubject extends AbstractSpongeSubject {

    public AbstractSystemSubject(@NotNull SubjectData data) {
        this.data = data;
    }

    private final SubjectData data;

    @Override
    @NotNull
    public SubjectData getSubjectData() {
        return this.data;
    }
}
