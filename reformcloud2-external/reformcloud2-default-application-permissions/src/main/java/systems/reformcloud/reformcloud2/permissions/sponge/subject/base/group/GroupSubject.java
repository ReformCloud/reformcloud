package systems.reformcloud.reformcloud2.permissions.sponge.subject.base.group;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectCollection;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.impl.AbstractGroupSubject;
import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;

import javax.annotation.Nonnull;

public class GroupSubject extends AbstractGroupSubject {

    public GroupSubject(@Nonnull String group, @Nonnull PermissionService service, @NonNull SubjectCollection source) {
        super(group);
        this.service = service;
        this.source = source;
        this.group = group;
    }

    private final PermissionService service;

    private final SubjectCollection source;

    private final String group;

    @Override
    protected PermissionService service() {
        return this.service;
    }

    @Override
    protected boolean has(String permission) {
        PermissionGroup permissionGroup = PermissionAPI.INSTANCE.getPermissionUtil().getGroup(group);
        if (permissionGroup == null) {
            return false;
        }

        return PermissionAPI.INSTANCE.getPermissionUtil().hasPermission(permissionGroup, permission.toLowerCase());
    }

    @Override
    @NonNull
    public SubjectCollection getContainingCollection() {
        return this.source;
    }

    @Override
    @Nonnull
    public String getIdentifier() {
        return this.group;
    }
}
