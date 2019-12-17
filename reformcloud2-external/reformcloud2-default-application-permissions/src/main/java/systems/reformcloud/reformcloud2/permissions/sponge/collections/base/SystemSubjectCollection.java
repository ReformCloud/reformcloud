package systems.reformcloud.reformcloud2.permissions.sponge.collections.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import systems.reformcloud.reformcloud2.permissions.sponge.collections.DefaultSubjectCollection;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.base.system.SystemSubject;

public class SystemSubjectCollection extends DefaultSubjectCollection {

  public SystemSubjectCollection(String type, PermissionService service) {
    super(type, service);
  }

  @Nonnull
  @Override
  protected Subject load(String id) {
    return new SystemSubject(id, service, this);
  }

  @Override
  @Nonnull
  public CompletableFuture<Boolean> hasSubject(@Nonnull String identifier) {
    return CompletableFuture.completedFuture(true);
  }

  @Override
  @Nonnull
  public Collection<Subject> getLoadedSubjects() {
    return new ArrayList<>();
  }
}
