package de.klaro.reformcloud2.permissions.util.group;

import com.google.gson.reflect.TypeToken;
import de.klaro.reformcloud2.permissions.util.basic.checks.GeneralCheck;
import de.klaro.reformcloud2.permissions.util.basic.checks.WildcardCheck;
import de.klaro.reformcloud2.permissions.util.permission.PermissionNode;

import java.util.Collection;
import java.util.Map;

public class PermissionGroup {

    public static final TypeToken<PermissionGroup> TYPE = new TypeToken<PermissionGroup>() {};

    public PermissionGroup(Collection<PermissionNode> permissionNodes, Map<String,
            Collection<PermissionNode>> perGroupPermissions,
                           Collection<String> subGroups, String name, int priority, long timeout) {
        this.permissionNodes = permissionNodes;
        this.perGroupPermissions = perGroupPermissions;
        this.subGroups = subGroups;
        this.name = name;
        this.priority = priority;
        this.timeout = timeout;
    }

    private final Collection<PermissionNode> permissionNodes;

    private final Map<String, Collection<PermissionNode>> perGroupPermissions;

    private final Collection<String> subGroups;

    private final String name;

    private final int priority;

    private final long timeout;

    public Collection<PermissionNode> getPermissionNodes() {
        return permissionNodes;
    }

    public Map<String, Collection<PermissionNode>> getPerGroupPermissions() {
        return perGroupPermissions;
    }

    public Collection<String> getSubGroups() {
        return subGroups;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isValid() {
        return timeout == -1 || timeout > System.currentTimeMillis();
    }

    public boolean hasPermission(String perm) {
        if (WildcardCheck.hasWildcardPermission(this, perm)) {
            return true;
        }

        return GeneralCheck.hasPermission(this, perm);
    }
}
