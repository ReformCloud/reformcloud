package systems.reformcloud.reformcloud2.permissions.sponge.description;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SpongePermissionDescription implements PermissionDescription {

    SpongePermissionDescription(
            @NotNull PermissionService service,
            @NotNull String id,
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
    @NotNull
    public String getId() {
        return id;
    }

    @Override
    @NotNull
    public Optional<Text> getDescription() {
        return Optional.ofNullable(description);
    }

    @Override
    @NotNull
    public Optional<PluginContainer> getOwner() {
        return Optional.ofNullable(owner);
    }

    @Override
    @NotNull
    public CompletableFuture<Map<SubjectReference, Boolean>> findAssignedSubjects(@NotNull String collectionIdentifier) {
        return service.loadCollection(collectionIdentifier).thenCompose(e -> {
            if (e == null) {
                return CompletableFuture.completedFuture(new HashMap<>());
            }

            return e.getAllWithPermission(getId());
        });
    }

    @Override
    @NotNull
    public Map<Subject, Boolean> getAssignedSubjects(@NotNull String collectionIdentifier) {
        return service.getCollection(collectionIdentifier).map(e -> e.getLoadedWithPermission(getId())).orElseGet(HashMap::new);
    }
}
