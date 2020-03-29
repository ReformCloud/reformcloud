package systems.reformcloud.reformcloud2.permissions.sponge.collections.base;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import systems.reformcloud.reformcloud2.permissions.sponge.collections.DefaultSubjectCollection;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.base.system.SystemSubject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class SystemSubjectCollection extends DefaultSubjectCollection {

    public SystemSubjectCollection(String type, PermissionService service) {
        super(type, service);
    }

    @NotNull
    @Override
    protected Subject load(String id) {
        return new SystemSubject(id, service, this);
    }

    @Override
    @NotNull
    public CompletableFuture<Boolean> hasSubject(@NotNull String identifier) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @NotNull
    public Collection<Subject> getLoadedSubjects() {
        return new ArrayList<>();
    }
}
