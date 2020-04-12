package systems.reformcloud.reformcloud2.permissions.sponge.collections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class DefaultSubjectCollection implements SubjectCollection {

    public DefaultSubjectCollection(String type, PermissionService service) {
        this.type = type;
        this.service = service;
    }

    private final String type;

    protected final PermissionService service;

    // ====

    @NotNull
    protected abstract Subject load(String id);

    // ===

    @Override
    @NotNull
    public String getIdentifier() {
        return type;
    }

    @Override
    @NotNull
    public Predicate<String> getIdentifierValidityPredicate() {
        return s -> true;
    }

    @Override
    @NotNull
    public SubjectReference newSubjectReference(@NotNull String subjectIdentifier) {
        return service.newSubjectReference(type, subjectIdentifier);
    }

    @Override
    @NotNull
    public CompletableFuture<Subject> loadSubject(@NotNull String identifier) {
        return CompletableFuture.completedFuture(load(identifier));
    }

    @Override
    @NotNull
    public Optional<Subject> getSubject(@NotNull String identifier) {
        return Optional.of(load(identifier));
    }

    @Override
    @NotNull
    public CompletableFuture<Map<String, Subject>> loadSubjects(@NotNull Set<String> identifiers) {
        Map<String, Subject> ref = new ConcurrentHashMap<>();
        identifiers.forEach(id -> ref.put(id, load(id)));
        return CompletableFuture.completedFuture(ref);
    }

    @Override
    @NotNull
    public Map<Subject, Boolean> getLoadedWithPermission(@NotNull String permission) {
        return getLoadedWithPermission(null, permission);
    }

    @Override
    @NotNull
    public Map<Subject, Boolean> getLoadedWithPermission(
            @Nullable Set<Context> contexts,
            @NotNull String permission
    ) {
        Map<Subject, Boolean> out = new ConcurrentHashMap<>();
        getLoadedSubjects().forEach(e -> {
            Tristate tristate = e.getPermissionValue(contexts == null ? e.getActiveContexts() : contexts, permission);
            if (tristate.equals(Tristate.UNDEFINED)) {
                return;
            }

            out.put(e, tristate.asBoolean());
        });
        return out;
    }

    @Override
    @NotNull
    public CompletableFuture<Map<SubjectReference, Boolean>> getAllWithPermission(@NotNull String permission) {
        return CompletableFuture.completedFuture(getLoadedWithPermission(permission).entrySet().stream().collect(Collectors.toMap(
                e -> e.getKey().asSubjectReference(),
                Map.Entry::getValue
        )));
    }

    @Override
    @NotNull
    public CompletableFuture<Map<SubjectReference, Boolean>> getAllWithPermission(@NotNull Set<Context> contexts, @NotNull String permission) {
        return CompletableFuture.completedFuture(getLoadedWithPermission(contexts, permission).entrySet().stream().collect(Collectors.toMap(
                e -> e.getKey().asSubjectReference(),
                Map.Entry::getValue
        )));
    }

    @Override
    @NotNull
    public CompletableFuture<Set<String>> getAllIdentifiers() {
        return CompletableFuture.completedFuture(getLoadedSubjects().stream()
                .map(Subject::getIdentifier)
                .collect(Collectors.toSet())
        );
    }

    @Override
    @NotNull
    public Subject getDefaults() {
        return this.service.getDefaults();
    }

    @Override
    public final void suggestUnload(@NotNull String identifier) {
    }
}
