package systems.reformcloud.reformcloud2.permissions.sponge.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.permission.*;
import systems.reformcloud.reformcloud2.permissions.sponge.collections.CollectionCatalog;
import systems.reformcloud.reformcloud2.permissions.sponge.description.SpongePermissionDescriptionBuilder;
import systems.reformcloud.reformcloud2.permissions.sponge.reference.SpongeSubjectReference;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.util.SubjectDefaultData;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class SpongePermissionService implements PermissionService {

    private static final Map<String, SubjectCollection> LOADED = new HashMap<>();

    public static final Map<String, PermissionDescription> DESCRIPTIONS = new ConcurrentHashMap<>();

    // ======

    static {
        LOADED.put(SUBJECTS_COMMAND_BLOCK, CollectionCatalog.COMMAND_BLOCK_COLLECTION);
        LOADED.put(SUBJECTS_SYSTEM, CollectionCatalog.SYSTEM_COLLECTION);
        LOADED.put(SUBJECTS_USER, CollectionCatalog.USER_COLLECTION);
        LOADED.put(SUBJECTS_DEFAULT, CollectionCatalog.FACTORY_COLLECTION);
        LOADED.put(SUBJECTS_GROUP, CollectionCatalog.GROUP_COLLECTION);
        LOADED.put(SUBJECTS_ROLE_TEMPLATE, CollectionCatalog.GROUP_COLLECTION);
    }

    // ======

    private static SpongePermissionService instance;

    public static SpongePermissionService getInstance() {
        return instance;
    }

    // ======

    public SpongePermissionService() {
        instance = this;
    }

    // ======

    @Override
    @NotNull
    public SubjectCollection getUserSubjects() {
        return CollectionCatalog.USER_COLLECTION;
    }

    @Override
    @NotNull
    public SubjectCollection getGroupSubjects() {
        return CollectionCatalog.GROUP_COLLECTION;
    }

    @Override
    @NotNull
    public Subject getDefaults() {
        return SubjectDefaultData.DEFAULT;
    }

    @Override
    @NotNull
    public Predicate<String> getIdentifierValidityPredicate() {
        return s -> true;
    }

    @Override
    @NotNull
    public CompletableFuture<SubjectCollection> loadCollection(@NotNull String identifier) {
        return CompletableFuture.completedFuture(LOADED.getOrDefault(identifier, CollectionCatalog.FACTORY_COLLECTION));
    }

    @Override
    @NotNull
    public Optional<SubjectCollection> getCollection(@NotNull String identifier) {
        return Optional.ofNullable(loadCollection(identifier).join());
    }

    @Override
    @NotNull
    public CompletableFuture<Boolean> hasCollection(@NotNull String identifier) {
        return CompletableFuture.completedFuture(getCollection(identifier).isPresent());
    }

    @Override
    @NotNull
    public Map<String, SubjectCollection> getLoadedCollections() {
        return Collections.unmodifiableMap(LOADED);
    }

    @Override
    @NotNull
    public CompletableFuture<Set<String>> getAllIdentifiers() {
        return CompletableFuture.completedFuture(LOADED.keySet());
    }

    @Override
    @NotNull
    public SubjectReference newSubjectReference(
            @NotNull String collectionIdentifier,
            @NotNull String subjectIdentifier
    ) {
        return new SpongeSubjectReference(this, collectionIdentifier, subjectIdentifier);
    }

    @Override
    @NotNull
    public PermissionDescription.Builder newDescriptionBuilder(@NotNull Object plugin) {
        return new SpongePermissionDescriptionBuilder(this, Sponge.getPluginManager().fromInstance(plugin).orElse(null));
    }

    @Override
    @NotNull
    public Optional<PermissionDescription> getDescription(@NotNull String permission) {
        return Optional.ofNullable(DESCRIPTIONS.get(permission));
    }

    @Override
    @NotNull
    public Collection<PermissionDescription> getDescriptions() {
        return DESCRIPTIONS.values();
    }

    @Override
    public void registerContextCalculator(@Nullable ContextCalculator<Subject> calculator) {
    }
}
