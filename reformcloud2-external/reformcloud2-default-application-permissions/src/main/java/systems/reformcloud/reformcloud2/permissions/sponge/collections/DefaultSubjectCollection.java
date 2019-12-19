package systems.reformcloud.reformcloud2.permissions.sponge.collections;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    @Nonnull
    protected abstract Subject load(String id);

    // ===

    @Override
    @Nonnull
    public String getIdentifier() {
        return type;
    }

    @Override
    @Nonnull
    public Predicate<String> getIdentifierValidityPredicate() {
        return s -> true;
    }

    @Override
    @Nonnull
    public SubjectReference newSubjectReference(@Nonnull String subjectIdentifier) {
        return service.newSubjectReference(type, subjectIdentifier);
    }

    @Override
    @Nonnull
    public CompletableFuture<Subject> loadSubject(@Nonnull String identifier) {
        return CompletableFuture.completedFuture(load(identifier));
    }

    @Override
    @Nonnull
    public Optional<Subject> getSubject(@Nonnull String identifier) {
        return Optional.of(load(identifier));
    }

    @Override
    @Nonnull
    public CompletableFuture<Map<String, Subject>> loadSubjects(@Nonnull Set<String> identifiers) {
        Map<String, Subject> ref = new ConcurrentHashMap<>();
        identifiers.forEach(id -> ref.put(id, load(id)));
        return CompletableFuture.completedFuture(ref);
    }

    @Override
    @Nonnull
    public Map<Subject, Boolean> getLoadedWithPermission(@Nonnull String permission) {
        return getLoadedWithPermission(null, permission);
    }

    @Override
    @Nonnull
    public Map<Subject, Boolean> getLoadedWithPermission(
            @Nullable Set<Context> contexts,
            @Nonnull String permission
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
    @Nonnull
    public CompletableFuture<Map<SubjectReference, Boolean>> getAllWithPermission(@Nonnull String permission) {
        return CompletableFuture.completedFuture(getLoadedWithPermission(permission).entrySet().stream().collect(Collectors.toMap(
                e -> e.getKey().asSubjectReference(),
                Map.Entry::getValue
        )));
    }

    @Override
    @Nonnull
    public CompletableFuture<Map<SubjectReference, Boolean>> getAllWithPermission(@Nonnull Set<Context> contexts, @Nonnull String permission) {
        return CompletableFuture.completedFuture(getLoadedWithPermission(contexts, permission).entrySet().stream().collect(Collectors.toMap(
                e -> e.getKey().asSubjectReference(),
                Map.Entry::getValue
        )));
    }

    @Override
    @Nonnull
    public CompletableFuture<Set<String>> getAllIdentifiers() {
        return CompletableFuture.completedFuture(getLoadedSubjects().stream()
                .map(Subject::getIdentifier)
                .collect(Collectors.toSet())
        );
    }

    @Override
    @Nonnull
    public Subject getDefaults() {
        return this.service.getDefaults();
    }

    @Override
    public final void suggestUnload(@Nonnull String identifier) {
    }
}
