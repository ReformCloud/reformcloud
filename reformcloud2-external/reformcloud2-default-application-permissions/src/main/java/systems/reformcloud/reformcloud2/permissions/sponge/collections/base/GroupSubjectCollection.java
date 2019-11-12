package systems.reformcloud.reformcloud2.permissions.sponge.collections.base;

import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.sponge.collections.DefaultSubjectCollection;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.base.group.GroupSubject;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class GroupSubjectCollection extends DefaultSubjectCollection {

    public GroupSubjectCollection(PermissionService service) {
        super(PermissionService.SUBJECTS_GROUP, service);
    }

    @Nonnull
    @Override
    protected Subject load(String id) {
        return new GroupSubject(id, service, this);
    }

    @Override
    @Nonnull
    public CompletableFuture<Boolean> hasSubject(@Nonnull String identifier) {
        return CompletableFuture.completedFuture(PermissionAPI.INSTANCE.getPermissionUtil().getGroup(identifier) != null);
    }

    @Override
    @Nonnull
    public Collection<Subject> getLoadedSubjects() {
        return new ArrayList<>();
    }
}
