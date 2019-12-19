package systems.reformcloud.reformcloud2.permissions.util.group;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.permissions.util.basic.checks.GeneralCheck;
import systems.reformcloud.reformcloud2.permissions.util.basic.checks.WildcardCheck;
import systems.reformcloud.reformcloud2.permissions.util.permission.PermissionNode;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;

public class PermissionGroup {

    public static final TypeToken<PermissionGroup> TYPE = new TypeToken<PermissionGroup>() {};

    public PermissionGroup(
            @Nonnull Collection<PermissionNode> permissionNodes,
            @Nonnull Map<String, Collection<PermissionNode>> perGroupPermissions,
            @Nonnull Collection<String> subGroups,
            @Nonnull String name,
            int priority
    ) {
        this.permissionNodes = permissionNodes;
        this.perGroupPermissions = perGroupPermissions;
        this.subGroups = subGroups;
        this.name = name;
        this.priority = priority;
    }

    private final Collection<PermissionNode> permissionNodes;

    private final Map<String, Collection<PermissionNode>> perGroupPermissions;

    private final Collection<String> subGroups;

    private final String name;

    private int priority;

    @Nonnull
    public Collection<PermissionNode> getPermissionNodes() {
        return permissionNodes;
    }

    @Nonnull
    public Map<String, Collection<PermissionNode>> getPerGroupPermissions() {
        return perGroupPermissions;
    }

    @Nonnull
    public Collection<String> getSubGroups() {
        return subGroups;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean hasPermission(@Nonnull String perm) {
        if (WildcardCheck.hasWildcardPermission(this, perm)) {
            return true;
        }

        return GeneralCheck.hasPermission(this, perm);
    }
}
