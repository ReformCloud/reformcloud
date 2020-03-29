package systems.reformcloud.reformcloud2.permissions.util.group;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.permissions.util.basic.checks.GeneralCheck;
import systems.reformcloud.reformcloud2.permissions.util.basic.checks.WildcardCheck;
import systems.reformcloud.reformcloud2.permissions.util.permission.PermissionNode;

import java.util.Collection;
import java.util.Map;

public class PermissionGroup {

    public static final TypeToken<PermissionGroup> TYPE = new TypeToken<PermissionGroup>() {};

    public PermissionGroup(
            @NotNull Collection<PermissionNode> permissionNodes,
            @NotNull Map<String, Collection<PermissionNode>> perGroupPermissions,
            @NotNull Collection<String> subGroups,
            @NotNull String name,
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

    @NotNull
    public Collection<PermissionNode> getPermissionNodes() {
        return permissionNodes;
    }

    @NotNull
    public Map<String, Collection<PermissionNode>> getPerGroupPermissions() {
        return perGroupPermissions;
    }

    @NotNull
    public Collection<String> getSubGroups() {
        return subGroups;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean hasPermission(@NotNull String perm) {
        if (WildcardCheck.hasWildcardPermission(this, perm)) {
            return true;
        }

        return GeneralCheck.hasPermission(this, perm);
    }
}
