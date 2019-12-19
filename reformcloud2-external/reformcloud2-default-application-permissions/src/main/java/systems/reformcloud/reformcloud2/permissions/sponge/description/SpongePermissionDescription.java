package systems.reformcloud.reformcloud2.permissions.sponge.description;

import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SpongePermissionDescription implements PermissionDescription {

    SpongePermissionDescription(
            @Nonnull PermissionService service,
            @Nonnull String id,
            @Nullable PluginContainer owner,
            @Nullable Text description
    ) {
        this.service = service;
        this.id = id;
        this.owner = owner;
        this.description = description;
    }

    private final PermissionService service;

    private final String id;

    private final PluginContainer owner;

    private final Text description;

    @Override
    @Nonnull
    public String getId() {
        return id;
    }

    @Override
    @Nonnull
    public Optional<Text> getDescription() {
        return Optional.ofNullable(description);
    }

    @Override
    @Nonnull
    public Optional<PluginContainer> getOwner() {
        return Optional.ofNullable(owner);
    }

    @Override
    @Nonnull
    public CompletableFuture<Map<SubjectReference, Boolean>> findAssignedSubjects(@Nonnull String collectionIdentifier) {
        return service.loadCollection(collectionIdentifier).thenCompose(e -> {
            if (e == null) {
                return CompletableFuture.completedFuture(new HashMap<>());
            }

            return e.getAllWithPermission(getId());
        });
    }

    @Override
    @Nonnull
    public Map<Subject, Boolean> getAssignedSubjects(@Nonnull String collectionIdentifier) {
        return service.getCollection(collectionIdentifier).map(e -> e.getLoadedWithPermission(getId())).orElseGet(HashMap::new);
    }
}
