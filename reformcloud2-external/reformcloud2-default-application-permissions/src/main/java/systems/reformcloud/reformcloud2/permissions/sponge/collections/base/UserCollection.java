package systems.reformcloud.reformcloud2.permissions.sponge.collections.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.permissions.sponge.collections.DefaultSubjectCollection;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.base.user.SpongeSubject;

public class UserCollection extends DefaultSubjectCollection {

  public UserCollection(PermissionService service) {
    super(PermissionService.SUBJECTS_USER, service);
  }

  @Nonnull
  @Override
  protected Subject load(String id) {
    UUID uniqueID = CommonHelper.tryParse(id);
    Conditions.isTrue(uniqueID != null);
    return new SpongeSubject(uniqueID, this, service);
  }

  @Override
  @Nonnull
  public CompletableFuture<Boolean> hasSubject(@Nonnull String identifier) {
    return CompletableFuture.completedFuture(true);
  }

  @Override
  @Nonnull
  public Collection<Subject> getLoadedSubjects() {
    return new ArrayList<>(Sponge.getServer().getOnlinePlayers());
  }
}
