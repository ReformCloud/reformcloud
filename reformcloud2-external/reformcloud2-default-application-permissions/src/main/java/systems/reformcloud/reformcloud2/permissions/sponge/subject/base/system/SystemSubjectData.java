package systems.reformcloud.reformcloud2.permissions.sponge.subject.base.system;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectData;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.AbstractSpongeSubjectData;

public class SystemSubjectData extends AbstractSpongeSubjectData {

  private static final Map<String, Boolean> PERMS =
      Collections.singletonMap("*", true);

  @Override
  @Nonnull
  public Map<Set<Context>, Map<String, Boolean>> getAllPermissions() {
    return Collections.singletonMap(SubjectData.GLOBAL_CONTEXT, PERMS);
  }

  @Override
  @Nonnull
  public Map<String, Boolean> getPermissions(@Nullable Set<Context> contexts) {
    return PERMS;
  }
}
