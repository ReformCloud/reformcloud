package systems.reformcloud.reformcloud2.permissions.sponge.subject.base.group;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectCollection;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.impl.AbstractGroupSubject;
import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;

public class GroupSubject extends AbstractGroupSubject {

    public GroupSubject(@NotNull String group, @NotNull PermissionService service, @NotNull SubjectCollection source) {
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
        PermissionGroup permissionGroup = PermissionAPI.getInstance().getPermissionUtil().getGroup(group);
        if (permissionGroup == null) {
            return false;
        }

        return PermissionAPI.getInstance().getPermissionUtil().hasPermission(permissionGroup, permission.toLowerCase());
    }

    @Override
    @NotNull
    public SubjectCollection getContainingCollection() {
        return this.source;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return this.group;
    }
}
