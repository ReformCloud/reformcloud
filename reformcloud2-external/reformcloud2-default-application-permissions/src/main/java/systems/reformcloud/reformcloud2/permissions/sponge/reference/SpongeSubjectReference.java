package systems.reformcloud.reformcloud2.permissions.sponge.reference;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectReference;

import java.util.concurrent.CompletableFuture;

public class SpongeSubjectReference implements SubjectReference {

    public SpongeSubjectReference(
            @NotNull PermissionService service,
            @NotNull String collection,
            @NotNull String id
    ) {
        this.collection = collection;
        this.id = id;
        this.service = service;
    }

    private final PermissionService service;

    private final String collection;

    private final String id;

    private Subject cache;

    @Override
    @NotNull
    public String getCollectionIdentifier() {
        return collection;
    }

    @Override
    @NotNull
    public String getSubjectIdentifier() {
        return id;
    }

    @Override
    @NotNull
    public CompletableFuture<Subject> resolve() {
        if (cache == null) {
            SubjectCollection subjectCollection = service.getCollection(collection).orElseThrow(() -> new IllegalArgumentException("Collection not loaded"));
            this.cache = subjectCollection.loadSubject(id).join();
        }

        return CompletableFuture.completedFuture(this.cache);
    }
}
