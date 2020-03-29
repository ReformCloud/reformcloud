package systems.reformcloud.reformcloud2.permissions.sponge.subject.base.group;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectData;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.AbstractSpongeSubjectData;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.util.SubjectGroupPermissionCalculator;
import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GroupSubjectData extends AbstractSpongeSubjectData {

    public GroupSubjectData(@NotNull String group) {
        this.group = group;
    }

    private final String group;

    @Override
    @NotNull
    public Map<Set<Context>, Map<String, Boolean>> getAllPermissions() {
        return Collections.singletonMap(SubjectData.GLOBAL_CONTEXT, getPermissions());
    }

    @Override
    @NotNull
    public Map<String, Boolean> getPermissions(@Nullable Set<Context> contexts) {
        return getPermissions();
    }

    private Map<String, Boolean> getPermissions() {
        Map<String, Boolean> out = new HashMap<>();
        PermissionGroup permissionGroup = PermissionAPI.getInstance().getPermissionUtil().getGroup(group);
        if (permissionGroup == null) {
            return out;
        }

        out.putAll(getPermissionsOf(permissionGroup));
        permissionGroup.getSubGroups().forEach(e -> {
            PermissionGroup sub = PermissionAPI.getInstance().getPermissionUtil().getGroup(e);
            if (sub == null) {
                return;
            }

            out.putAll(getPermissionsOf(sub));
        });

        return out;
    }

    private Map<String, Boolean> getPermissionsOf(PermissionGroup group) {
        return SubjectGroupPermissionCalculator.getPermissionsOf(group);
    }
}
