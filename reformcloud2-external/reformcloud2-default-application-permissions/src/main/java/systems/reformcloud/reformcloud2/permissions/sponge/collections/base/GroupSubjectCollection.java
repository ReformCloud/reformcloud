package systems.reformcloud.reformcloud2.permissions.sponge.collections.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.sponge.collections.DefaultSubjectCollection;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.base.group.GroupSubject;

public class GroupSubjectCollection extends DefaultSubjectCollection {

  public GroupSubjectCollection(PermissionService service) {
    super(PermissionService.SUBJECTS_GROUP, service);
  }

  @Nonnull
  @Override
  protected Subject load(String id) {
    return new GroupSubject(id, service, this);
  }

  @Override
  @Nonnull
  public CompletableFuture<Boolean> hasSubject(@Nonnull String identifier) {
    return CompletableFuture.completedFuture(
        PermissionAPI.getInstance().getPermissionUtil().getGroup(identifier) !=
        null);
  }

  @Override
  @Nonnull
  public Collection<Subject> getLoadedSubjects() {
    return new ArrayList<>();
  }
}
