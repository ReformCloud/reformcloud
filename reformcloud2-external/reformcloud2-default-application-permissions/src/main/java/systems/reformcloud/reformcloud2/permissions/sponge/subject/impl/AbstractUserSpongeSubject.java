package systems.reformcloud.reformcloud2.permissions.sponge.subject.impl;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.service.permission.SubjectData;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.AbstractSpongeSubject;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.base.user.SpongeSubjectData;

import java.util.UUID;

public abstract class AbstractUserSpongeSubject extends AbstractSpongeSubject {

    public AbstractUserSpongeSubject(@NotNull UUID user) {
        this.userSubjectData = new SpongeSubjectData(user);
    }

    private final SubjectData userSubjectData;

    @Override
    @NotNull
    public SubjectData getSubjectData() {
        return this.userSubjectData;
    }
}
