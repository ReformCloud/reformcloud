package systems.reformcloud.reformcloud2.permissions.sponge.subject.impl;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.service.permission.SubjectData;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.AbstractSpongeSubject;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.base.group.GroupSubjectData;

public abstract class AbstractGroupSubject extends AbstractSpongeSubject {

    public AbstractGroupSubject(@NotNull String group) {
        this.subjectData = new GroupSubjectData(group);
    }

    private final SubjectData subjectData;

    @Override
    @NotNull
    public SubjectData getSubjectData() {
        return this.subjectData;
    }
}
