package systems.reformcloud.reformcloud2.permissions.sponge.subject.impl;

import org.spongepowered.api.service.permission.SubjectData;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.AbstractSpongeSubject;

import javax.annotation.Nonnull;

public abstract class AbstractSystemSubject extends AbstractSpongeSubject {

    public AbstractSystemSubject(@Nonnull SubjectData data) {
        this.data = data;
    }

    private final SubjectData data;

    @Override
    @Nonnull
    public SubjectData getSubjectData() {
        return this.data;
    }
}
