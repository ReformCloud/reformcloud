package systems.reformcloud.reformcloud2.permissions.sponge.subject;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSpongeSubjectData implements SubjectData {

    @Override
    @Nonnull
    public CompletableFuture<Boolean> setPermission(@Nullable Set<Context> contexts, @Nullable String permission, @Nullable Tristate value) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @Nonnull
    public CompletableFuture<Boolean> clearPermissions() {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @Nonnull
    public CompletableFuture<Boolean> clearPermissions(@Nullable Set<Context> contexts) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @Nonnull
    public CompletableFuture<Boolean> addParent(@Nullable Set<Context> contexts, @Nullable SubjectReference parent) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @Nonnull
    public CompletableFuture<Boolean> removeParent(@Nullable Set<Context> contexts, @Nullable SubjectReference parent) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @Nonnull
    public CompletableFuture<Boolean> clearParents() {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @Nonnull
    public CompletableFuture<Boolean> clearParents(@Nullable Set<Context> contexts) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @Nonnull
    public Map<Set<Context>, Map<String, String>> getAllOptions() {
        return new ConcurrentHashMap<>();
    }

    @Override
    @Nonnull
    public Map<String, String> getOptions(@Nullable Set<Context> contexts) {
        return new ConcurrentHashMap<>();
    }

    @Override
    @Nonnull
    public CompletableFuture<Boolean> setOption(@Nullable Set<Context> contexts, @Nullable String key, @Nullable String value) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @Nonnull
    public CompletableFuture<Boolean> clearOptions() {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @Nonnull
    public CompletableFuture<Boolean> clearOptions(@Nullable Set<Context> contexts) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @Nonnull
    public List<SubjectReference> getParents(@Nullable Set<Context> contexts) {
        return new ArrayList<>();
    }

    @Override
    @Nonnull
    public Map<Set<Context>, List<SubjectReference>> getAllParents() {
        return new ConcurrentHashMap<>();
    }
}
