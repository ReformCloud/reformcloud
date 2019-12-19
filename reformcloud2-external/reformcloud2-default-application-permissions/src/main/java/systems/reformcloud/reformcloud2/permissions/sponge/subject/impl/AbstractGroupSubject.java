package systems.reformcloud.reformcloud2.permissions.sponge.subject.impl;

import javax.annotation.Nonnull;
import org.spongepowered.api.service.permission.SubjectData;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.AbstractSpongeSubject;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.base.group.GroupSubjectData;

public abstract class AbstractGroupSubject extends AbstractSpongeSubject {

  public AbstractGroupSubject(@Nonnull String group) {
    this.subjectData = new GroupSubjectData(group);
  }

  private final SubjectData subjectData;

  @Override
  @Nonnull
  public SubjectData getSubjectData() {
    return this.subjectData;
  }
}
