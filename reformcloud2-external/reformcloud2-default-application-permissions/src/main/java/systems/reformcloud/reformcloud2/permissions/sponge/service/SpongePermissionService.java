package systems.reformcloud.reformcloud2.permissions.sponge.service;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.permission.*;
import systems.reformcloud.reformcloud2.permissions.sponge.collections.CollectionCatalog;
import systems.reformcloud.reformcloud2.permissions.sponge.description.SpongePermissionDescriptionBuilder;
import systems.reformcloud.reformcloud2.permissions.sponge.reference.SpongeSubjectReference;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.util.SubjectDefaultData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    @Nonnull
    public SubjectCollection getUserSubjects() {
        return CollectionCatalog.USER_COLLECTION;
    }

    @Override
    @Nonnull
    public SubjectCollection getGroupSubjects() {
        return CollectionCatalog.GROUP_COLLECTION;
    }

    @Override
    @Nonnull
    public Subject getDefaults() {
        return SubjectDefaultData.DEFAULT;
    }

    @Override
    @Nonnull
    public Predicate<String> getIdentifierValidityPredicate() {
        return s -> true;
    }

    @Override
    @Nonnull
    public CompletableFuture<SubjectCollection> loadCollection(@Nonnull String identifier) {
        return CompletableFuture.completedFuture(LOADED.getOrDefault(identifier, CollectionCatalog.FACTORY_COLLECTION));
    }

    @Override
    @Nonnull
    public Optional<SubjectCollection> getCollection(@Nonnull String identifier) {
        return Optional.ofNullable(loadCollection(identifier).join());
    }

    @Override
    @Nonnull
    public CompletableFuture<Boolean> hasCollection(@Nonnull String identifier) {
        return CompletableFuture.completedFuture(getCollection(identifier).isPresent());
    }

    @Override
    @Nonnull
    public Map<String, SubjectCollection> getLoadedCollections() {
        return Collections.unmodifiableMap(LOADED);
    }

    @Override
    @Nonnull
    public CompletableFuture<Set<String>> getAllIdentifiers() {
        return CompletableFuture.completedFuture(LOADED.keySet());
    }

    @Override
    @Nonnull
    public SubjectReference newSubjectReference(
            @Nonnull String collectionIdentifier,
            @Nonnull String subjectIdentifier
    ) {
        return new SpongeSubjectReference(this, collectionIdentifier, subjectIdentifier);
    }

    @Override
    @Nonnull
    public PermissionDescription.Builder newDescriptionBuilder(@Nonnull Object plugin) {
        return new SpongePermissionDescriptionBuilder(this, Sponge.getPluginManager().fromInstance(plugin).orElse(null));
    }

    @Override
    @Nonnull
    public Optional<PermissionDescription> getDescription(@Nonnull String permission) {
        return Optional.ofNullable(DESCRIPTIONS.get(permission));
    }

    @Override
    @Nonnull
    public Collection<PermissionDescription> getDescriptions() {
        return DESCRIPTIONS.values();
    }

    @Override
    public void registerContextCalculator(@Nullable ContextCalculator<Subject> calculator) {
    }
}
