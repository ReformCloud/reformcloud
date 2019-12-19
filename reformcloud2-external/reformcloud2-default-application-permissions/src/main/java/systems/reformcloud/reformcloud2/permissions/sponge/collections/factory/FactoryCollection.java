package systems.reformcloud.reformcloud2.permissions.sponge.collections.factory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import systems.reformcloud.reformcloud2.permissions.sponge.collections.DefaultSubjectCollection;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.factory.FactorySubject;

public class FactoryCollection extends DefaultSubjectCollection {

  public FactoryCollection(PermissionService service) {
    super(PermissionService.SUBJECTS_DEFAULT, service);
  }

  private final Map<String, Subject> cache = new ConcurrentHashMap<>();

  @Nonnull
  @Override
  protected Subject load(String id) {
    cache.putIfAbsent(id, new FactorySubject(id, service, this));
    return cache.get(id);
  }

  @Override
  @Nonnull
  public CompletableFuture<Boolean> hasSubject(@Nonnull String identifier) {
    return CompletableFuture.completedFuture(cache.containsKey(identifier));
  }

  @Override
  @Nonnull
  public Collection<Subject> getLoadedSubjects() {
    return cache.values();
  }
}
