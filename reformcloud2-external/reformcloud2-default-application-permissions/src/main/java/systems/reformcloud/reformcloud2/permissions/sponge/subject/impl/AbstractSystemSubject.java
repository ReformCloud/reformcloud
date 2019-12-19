package systems.reformcloud.reformcloud2.permissions.sponge.subject.impl;

import javax.annotation.Nonnull;
import org.spongepowered.api.service.permission.SubjectData;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.AbstractSpongeSubject;

public abstract class AbstractSystemSubject extends AbstractSpongeSubject {

  public AbstractSystemSubject(@Nonnull SubjectData data) { this.data = data; }

  private final SubjectData data;

  @Override
  @Nonnull
  public SubjectData getSubjectData() {
    return this.data;
  }
}
