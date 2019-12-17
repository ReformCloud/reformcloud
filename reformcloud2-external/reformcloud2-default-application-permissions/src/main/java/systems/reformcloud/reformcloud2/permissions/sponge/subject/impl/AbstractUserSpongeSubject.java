package systems.reformcloud.reformcloud2.permissions.sponge.subject.impl;

import java.util.UUID;
import javax.annotation.Nonnull;
import org.spongepowered.api.service.permission.SubjectData;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.AbstractSpongeSubject;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.base.user.SpongeSubjectData;

public abstract class AbstractUserSpongeSubject extends AbstractSpongeSubject {

  public AbstractUserSpongeSubject(@Nonnull UUID user) {
    this.userSubjectData = new SpongeSubjectData(user);
  }

  private final SubjectData userSubjectData;

  @Override
  @Nonnull
  public SubjectData getSubjectData() {
    return this.userSubjectData;
  }
}
