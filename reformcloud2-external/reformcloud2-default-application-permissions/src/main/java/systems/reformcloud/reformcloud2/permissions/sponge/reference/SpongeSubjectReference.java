package systems.reformcloud.reformcloud2.permissions.sponge.reference;

import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectReference;

public class SpongeSubjectReference implements SubjectReference {

  public SpongeSubjectReference(@Nonnull PermissionService service,
                                @Nonnull String collection,
                                @Nonnull String id) {
    this.collection = collection;
    this.id = id;
    this.service = service;
  }

  private final PermissionService service;

  private final String collection;

  private final String id;

  private Subject cache;

  @Override
  @Nonnull
  public String getCollectionIdentifier() {
    return collection;
  }

  @Override
  @Nonnull
  public String getSubjectIdentifier() {
    return id;
  }

  @Override
  @Nonnull
  public CompletableFuture<Subject> resolve() {
    if (cache == null) {
      SubjectCollection subjectCollection =
          service.getCollection(collection)
              .orElseThrow(
                  () -> new IllegalArgumentException("Collection not loaded"));
      this.cache = subjectCollection.loadSubject(id).join();
    }

    return CompletableFuture.completedFuture(this.cache);
  }
}
