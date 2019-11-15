package systems.reformcloud.reformcloud2.permissions.application.command;

import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.util.group.NodeGroup;
import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.util.permission.PermissionNode;
import systems.reformcloud.reformcloud2.permissions.util.unit.InternalTimeUnit;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;
import systems.reformcloud.reformcloud2.permissions.util.uuid.UUIDFetcher;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CommandPerms extends GlobalCommand {

    private static final String[] HELP = new String[] {
            "perms group [groupname]",
            "perms group [groupname] create",
            "perms group [groupname] create [default]",
            "perms group [groupname] delete",
            "perms group [groupname] clear",
            "perms group [groupname] setdefault [default]",
            "perms group [groupname] addperm [permission] [set]",
            "perms group [groupname] addperm [permission] [set] [timeout] [s/m/h/d/mo]",
            "perms group [groupname] addperm [processgroup] [permission] [set]",
            "perms group [groupname] addperm [processgroup] [permission] [set] [timeout] [s/m/h/d/mo]",
            "perms group [groupname] delperm [permission]",
            "perms group [groupname] delperm [processgroup] [permission]",
            "perms group [groupname] parent add [groupname]",
            "perms group [groupname] parent remove [groupname]",
            "perms group [groupname] parent clear",
            " ",
            "perms user [user]",
            "perms user [user] delete",
            "perms user [user] clear",
            "perms user [user] addperm [permission] [set]",
            "perms user [user] addperm [permission] [set] [timeout] [s/m/h/d/mo]",
            "perms user [user] delperm [permission]",
            "perms user [user] addgroup [group]",
            "perms user [user] addgroup [group] [timeout] [s/m/h/d/mo]",
            "perms user [user] delgroup [group]"
    };

    public CommandPerms() {
        super("perms", "reformcloud.command.perms",
                "The main perms command", Arrays.asList("permissions", "cloudperms"));
    }

    @Override
    public boolean handleCommand(@Nonnull CommandSource commandSource, @Nonnull String[] strings) {
        if (strings.length == 2 && strings[0].equalsIgnoreCase("user")) {
            UUID uniqueID = UUIDFetcher.getUUIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSource.sendMessage("The uniqueID is unknown");
                return true;
            }

            PermissionUser user = PermissionAPI.getInstance().getPermissionUtil().loadUser(uniqueID);
            {
                StringBuilder stringBuilder = new StringBuilder();
                for (NodeGroup group : user.getGroups()) {
                    if (group.isValid()) {
                        stringBuilder.append(group.getGroupName()).append(", ");
                    }
                }
                commandSource.sendMessage("Groups: " + (stringBuilder.length() > 2
                        ? stringBuilder.substring(0, stringBuilder.length() -2) : "none"));
            }
            {
                StringBuilder stringBuilder = new StringBuilder();
                for (PermissionNode permissionNode : user.getPermissionNodes()) {
                    if (permissionNode.isValid()) {
                        stringBuilder.append(permissionNode.isSet()
                                ? permissionNode.getActualPermission()
                                : "-" + permissionNode.getActualPermission()).append(", ");
                    }
                }
                commandSource.sendMessage("Permissions: " + (stringBuilder.length() > 2
                        ? stringBuilder.substring(0, stringBuilder.length() -2) : "none"));
            }

            return true;
        }

        if (strings.length == 3
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("delete")
        ) {
            UUID uniqueID = UUIDFetcher.getUUIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSource.sendMessage("The uniqueID is unknown");
                return true;
            }

            PermissionAPI.getInstance().getPermissionUtil().deleteUser(uniqueID);
            System.out.println("Deleted user " + strings[1]);
            return true;
        }

        if (strings.length == 3
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("clear")
        ) {
            UUID uniqueID = UUIDFetcher.getUUIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSource.sendMessage("The uniqueID is unknown");
                return true;
            }

            PermissionUser user = PermissionAPI.getInstance().getPermissionUtil().loadUser(uniqueID);
            user.getPermissionNodes().clear();
            user.getGroups().clear();
            PermissionAPI.getInstance().getPermissionUtil().updateUser(user);
            System.out.println("Cleared all groups and permissions of user " + strings[1]);
            return true;
        }

        if (strings.length == 5
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("addperm")
        ) {
            UUID uniqueID = UUIDFetcher.getUUIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSource.sendMessage("The uniqueID is unknown");
                return true;
            }

            PermissionUser user = PermissionAPI.getInstance().getPermissionUtil().loadUser(uniqueID);
            if (Links.filterToReference(user.getPermissionNodes(),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3])).isPresent()) {
                System.out.println("The permission " + strings[3] + " is already set");
                return true;
            }

            Boolean set = CommonHelper.booleanFromString(strings[4]);
            if (set == null) {
                System.out.println("The permission may not be set correctly. Please recheck (use true/false as set argument)");
                return true;
            }

            user.getPermissionNodes().add(new PermissionNode(
                    System.currentTimeMillis(),
                    -1,
                    set,
                    strings[3]
            ));
            PermissionAPI.getInstance().getPermissionUtil().updateUser(user);
            System.out.println("The permission " + strings[3] + " was added to the user " + strings[1] + " with value " + set);
            return true;
        }

        if (strings.length == 7
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("addperm")
        ) {
            UUID uniqueID = UUIDFetcher.getUUIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSource.sendMessage("The uniqueID is unknown");
                return true;
            }

            PermissionUser user = PermissionAPI.getInstance().getPermissionUtil().loadUser(uniqueID);
            if (Links.filterToReference(user.getPermissionNodes(),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3])).isPresent()) {
                System.out.println("The permission " + strings[3] + " is already set");
                return true;
            }

            Boolean set = CommonHelper.booleanFromString(strings[4]);
            if (set == null) {
                System.out.println("The permission may not be set correctly. Please recheck (use true/false as set argument)");
                return true;
            }

            Long givenTimeOut = CommonHelper.longFromString(strings[5]);
            if (givenTimeOut == null) {
                System.out.println("The timout time is not valid");
                return true;
            }

            long timeOut = System.currentTimeMillis()
                    + InternalTimeUnit.convert(parseUnitFromString(strings[6]), givenTimeOut);
            user.getPermissionNodes().add(new PermissionNode(
                    System.currentTimeMillis(),
                    timeOut,
                    set,
                    strings[3]
            ));
            PermissionAPI.getInstance().getPermissionUtil().updateUser(user);
            System.out.println("The permission " + strings[3] + " was added to the user " + strings[1] + " with value " + set);
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("delperm")
        ) {
            UUID uniqueID = UUIDFetcher.getUUIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSource.sendMessage("The uniqueID is unknown");
                return true;
            }

            PermissionUser user = PermissionAPI.getInstance().getPermissionUtil().loadUser(uniqueID);
            ReferencedOptional<PermissionNode> perm = Links.filterToReference(user.getPermissionNodes(),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3]));
            if (!perm.isPresent()) {
                System.out.println("The permission " + strings[3] + " is not set");
                return true;
            }

            user.getPermissionNodes().remove(perm.get());
            PermissionAPI.getInstance().getPermissionUtil().updateUser(user);
            System.out.println("Removed permission " + strings[3] + " from user " + strings[1]);
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("addgroup")
        ) {
            UUID uniqueID = UUIDFetcher.getUUIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSource.sendMessage("The uniqueID is unknown");
                return true;
            }

            PermissionGroup group = PermissionAPI.getInstance().getPermissionUtil().getGroup(strings[3]);
            if (group == null) {
                System.out.println("The group " + strings[3] + " does not exists");
                return true;
            }

            PermissionUser user = PermissionAPI.getInstance().getPermissionUtil().loadUser(uniqueID);
            if (Links.filterToReference(user.getGroups(), e -> e.getGroupName().equals(strings[3]) && e.isValid()).isPresent()) {
                System.out.println("The user " + strings[1] + " is already in group " + strings[3]);
                return true;
            }

            user.getGroups().add(new NodeGroup(
                    System.currentTimeMillis(),
                    -1,
                    group.getName()
            ));
            PermissionAPI.getInstance().getPermissionUtil().updateUser(user);
            System.out.println("Successfully added user " + strings[1] + " to group " + strings[3]);
            return true;
        }

        if (strings.length == 6
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("addgroup")
        ) {
            UUID uniqueID = UUIDFetcher.getUUIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSource.sendMessage("The uniqueID is unknown");
                return true;
            }

            PermissionGroup group = PermissionAPI.getInstance().getPermissionUtil().getGroup(strings[3]);
            if (group == null) {
                System.out.println("The group " + strings[3] + " does not exists");
                return true;
            }

            PermissionUser user = PermissionAPI.getInstance().getPermissionUtil().loadUser(uniqueID);
            if (Links.filterToReference(user.getGroups(), e -> e.getGroupName().equals(strings[3]) && e.isValid()).isPresent()) {
                System.out.println("The user " + strings[1] + " is already in group " + strings[3]);
                return true;
            }

            Long givenTimeOut = CommonHelper.longFromString(strings[4]);
            if (givenTimeOut == null) {
                System.out.println("The timout time is not valid");
                return true;
            }

            long timeOut = System.currentTimeMillis()
                    +  InternalTimeUnit.convert(parseUnitFromString(strings[5]), givenTimeOut);
            user.getGroups().add(new NodeGroup(
                    System.currentTimeMillis(),
                    timeOut,
                    group.getName()
            ));
            PermissionAPI.getInstance().getPermissionUtil().updateUser(user);
            System.out.println("Successfully added user " + strings[1] + " to group " + strings[3]);
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("delgroup")
        ) {
            UUID uniqueID = UUIDFetcher.getUUIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSource.sendMessage("The uniqueID is unknown");
                return true;
            }

            PermissionUser user = PermissionAPI.getInstance().getPermissionUtil().loadUser(uniqueID);
            NodeGroup filter = Links.filter(user.getGroups(), e -> e.getGroupName().equals(strings[3]));
            if (filter == null) {
                System.out.println("The user " + strings[1] + " is not in group " + strings[3]);
                return true;
            }

            user.getGroups().remove(filter);
            PermissionAPI.getInstance().getPermissionUtil().updateUser(user);
            System.out.println("Successfully removed group " + strings[3] + " from user " + strings[1]);
            return true;
        }

        // ======== Groups ========

        if (strings.length == 2 && strings[0].equalsIgnoreCase("group")) {
            PermissionGroup group = PermissionAPI.getInstance().getPermissionUtil().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            {
                StringBuilder stringBuilder = new StringBuilder();
                for (PermissionNode permissionNode : group.getPermissionNodes()) {
                    if (!permissionNode.isValid()) {
                        continue;
                    }

                    stringBuilder
                            .append(permissionNode.isSet() ? "" : "-")
                            .append(permissionNode.getActualPermission())
                            .append(", ");
                }

                System.out.println("Permissions: " + (stringBuilder.length() > 2
                        ? stringBuilder.substring(0, stringBuilder.length() -2)
                        : "none"));
            }
            {
                StringBuilder stringBuilder = new StringBuilder();
                group.getPerGroupPermissions().forEach((k, v) -> {
                    stringBuilder.append("Group ").append(k).append(":\n");
                    v.forEach(e -> {
                        if (!e.isValid()) {
                            return;
                        }

                        stringBuilder.append("   - ").append(e.isSet() ? "" : "-").append(e.getActualPermission()).append(", ");
                    });
                    stringBuilder.append("\n");
                });
                System.out.println("Per-Group-Permissions: \n" + (stringBuilder.length() > 3
                        ? stringBuilder.substring(0, stringBuilder.length() -3) : "none"));
            }
            return true;
        }

        if (strings.length == 3
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("create")
        ) {
            PermissionGroup group = PermissionAPI.getInstance().getPermissionUtil().getGroup(strings[1]);
            if (group != null) {
                System.out.println("The group " + strings[1] + " already exists");
                return true;
            }

            PermissionAPI.getInstance().getPermissionUtil().createGroup(strings[1]);
            System.out.println("The group " + strings[1] + " was created successfully");
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("create")
        ) {
            PermissionGroup group = PermissionAPI.getInstance().getPermissionUtil().getGroup(strings[1]);
            if (group != null) {
                System.out.println("The group " + strings[1] + " already exists");
                return true;
            }

            Boolean defaultGroup = CommonHelper.booleanFromString(strings[3]);
            if (defaultGroup == null) {
                System.out.println("Please recheck (use true/false as 4 argument)");
                return true;
            }

            PermissionGroup created = PermissionAPI.getInstance().getPermissionUtil().createGroup(strings[1]);
            if (defaultGroup) {
                PermissionAPI.getInstance().getPermissionUtil().addDefaultGroup(created.getName());
            }

            System.out.println("The group " + strings[1] + " was created successfully");
            return true;
        }

        if (strings.length == 3
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("delete")
        ) {
            PermissionGroup group = PermissionAPI.getInstance().getPermissionUtil().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            PermissionAPI.getInstance().getPermissionUtil().deleteGroup(group.getName());
            System.out.println("The group " + strings[1] + " was deleted successfully");
            return true;
        }

        if (strings.length == 3
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("clear")
        ) {
            PermissionGroup group = PermissionAPI.getInstance().getPermissionUtil().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            group.getPerGroupPermissions().clear();
            group.getPermissionNodes().clear();
            PermissionAPI.getInstance().getPermissionUtil().updateGroup(group);
            System.out.println("Successfully deleted all permissions and process-group-permissions from group " + strings[1]);
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("setdefault")
        ) {
            PermissionGroup group = PermissionAPI.getInstance().getPermissionUtil().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            Boolean defaultGroup = CommonHelper.booleanFromString(strings[3]);
            if (defaultGroup == null) {
                System.out.println("Please recheck (use true/false as 4 argument)");
                return true;
            }

            if (defaultGroup) {
                PermissionAPI.getInstance().getPermissionUtil().addDefaultGroup(group.getName());
            } else {
                PermissionAPI.getInstance().getPermissionUtil().removeDefaultGroup(group.getName());
            }

            System.out.println("The group " + group.getName() + " is now a " + (defaultGroup ? "default" : "normal") + " group");
            return true;
        }

        if (strings.length == 5
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("addperm")
        ) {
            PermissionGroup group = PermissionAPI.getInstance().getPermissionUtil().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            if (Links.filterToReference(group.getPermissionNodes(),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3])).isPresent()) {
                System.out.println("The permission " + strings[3] + " is already set for group " + strings[3]);
                return true;
            }

            Boolean set = CommonHelper.booleanFromString(strings[4]);
            if (set == null) {
                System.out.println("The permission may not be set correctly. Please recheck (use true/false as set argument)");
                return true;
            }

            group.getPermissionNodes().add(new PermissionNode(
                    System.currentTimeMillis(),
                    -1,
                    set,
                    strings[3]
            ));
            PermissionAPI.getInstance().getPermissionUtil().updateGroup(group);
            System.out.println("The permission " + strings[3] + " was added to group " + group.getName());
            return true;
        }

        if (strings.length == 7
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("addperm")
        ) {
            PermissionGroup group = PermissionAPI.getInstance().getPermissionUtil().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            if (Links.filterToReference(group.getPermissionNodes(),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3])).isPresent()) {
                System.out.println("The permission " + strings[3] + " is already set for group " + strings[3]);
                return true;
            }

            Boolean set = CommonHelper.booleanFromString(strings[4]);
            if (set == null) {
                System.out.println("The permission may not be set correctly. Please recheck (use true/false as set argument)");
                return true;
            }

            Long givenTimeOut = CommonHelper.longFromString(strings[5]);
            if (givenTimeOut == null) {
                System.out.println("The timout time is not valid");
                return true;
            }

            long timeOut = System.currentTimeMillis()
                    +  InternalTimeUnit.convert(parseUnitFromString(strings[6]), givenTimeOut);
            group.getPermissionNodes().add(new PermissionNode(
                    System.currentTimeMillis(),
                    timeOut,
                    set,
                    strings[3]
            ));
            PermissionAPI.getInstance().getPermissionUtil().updateGroup(group);
            System.out.println("The permission " + strings[3] + " was added to group " + group.getName());
            return true;
        }

        if (strings.length == 6
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("addperm")
        ) {
            PermissionGroup group = PermissionAPI.getInstance().getPermissionUtil().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            if (group.getPerGroupPermissions().containsKey(strings[3])
                    && Links.filterToReference(group.getPerGroupPermissions().get(strings[3]),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3])).isPresent()) {
                System.out.println("The permission " + strings[4] + " is already set for group " + strings[2] + " on " + strings[3]);
                return true;
            }

            Boolean set = CommonHelper.booleanFromString(strings[5]);
            if (set == null) {
                System.out.println("The permission may not be set correctly. Please recheck (use true/false as set argument)");
                return true;
            }

            PermissionAPI.getInstance().getPermissionUtil().addProcessGroupPermission(strings[3], group, new PermissionNode(
                    System.currentTimeMillis(),
                    -1,
                    set,
                    strings[4]
            ));
            System.out.println("The permission " + strings[4] + " was added to group " + group.getName() + " on " + strings[3]);
            return true;
        }

        if (strings.length == 8
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("addperm")
        ) {
            PermissionGroup group = PermissionAPI.getInstance().getPermissionUtil().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            if (group.getPerGroupPermissions().containsKey(strings[3])
                    && Links.filterToReference(group.getPerGroupPermissions().get(strings[3]),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3])).isPresent()) {
                System.out.println("The permission " + strings[4] + " is already set for group " + strings[2] + " on " + strings[3]);
                return true;
            }

            Boolean set = CommonHelper.booleanFromString(strings[5]);
            if (set == null) {
                System.out.println("The permission may not be set correctly. Please recheck (use true/false as set argument)");
                return true;
            }

            Long givenTimeOut = CommonHelper.longFromString(strings[6]);
            if (givenTimeOut == null) {
                System.out.println("The timout time is not valid");
                return true;
            }

            long timeOut = System.currentTimeMillis()
                    +  InternalTimeUnit.convert(parseUnitFromString(strings[7]), givenTimeOut);
            PermissionAPI.getInstance().getPermissionUtil().addProcessGroupPermission(strings[3], group, new PermissionNode(
                    System.currentTimeMillis(),
                    timeOut,
                    set,
                    strings[4]
            ));
            System.out.println("The permission " + strings[4] + " was added to group " + group.getName() + " on " + strings[3]);
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("delperm")
        ) {
            PermissionGroup group = PermissionAPI.getInstance().getPermissionUtil().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            PermissionNode filter = Links.filter(group.getPermissionNodes(),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3]));
            if (filter == null) {
                System.out.println("The permission " + strings[3] + " is not set");
                return true;
            }

            group.getPermissionNodes().remove(filter);
            PermissionAPI.getInstance().getPermissionUtil().updateGroup(group);
            System.out.println("The permission " + strings[3] + " was removed from the group " + group.getName());
            return true;
        }

        if (strings.length == 5
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("delperm")
        ) {
            PermissionGroup group = PermissionAPI.getInstance().getPermissionUtil().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            if (!group.getPerGroupPermissions().containsKey(strings[3])) {
                System.out.println("There are no server group permission for group " + group.getName() + " on " + strings[3]);
                return true;
            }

            PermissionNode filter = Links.filter(group.getPerGroupPermissions().get(strings[3]),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[4]));
            if (filter == null) {
                System.out.println("The permission " + strings[4] + " is not set for " + group.getName() + " on " + strings[3]);
                return true;
            }

            group.getPerGroupPermissions().get(strings[3]).remove(filter);
            PermissionAPI.getInstance().getPermissionUtil().updateGroup(group);
            System.out.println("The permission " + strings[4] + " was removed for group " + group.getName() + " on " + strings[3]);
            return true;
        }

        if (strings.length == 5
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("parent")
                && strings[3].equalsIgnoreCase("add")
        ) {
            PermissionGroup group = PermissionAPI.getInstance().getPermissionUtil().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            if (group.getSubGroups().contains(strings[4])) {
                System.out.println("The group " + strings[4] + " is already a parent of " + group.getName());
                return true;
            }

            PermissionGroup sub = PermissionAPI.getInstance().getPermissionUtil().getGroup(strings[4]);
            if (sub == null) {
                System.out.println("The group " + strings[4] + " does not exists");
                return true;
            }

            group.getSubGroups().add(sub.getName());
            PermissionAPI.getInstance().getPermissionUtil().updateGroup(group);
            System.out.println("The sub group " + sub.getName() + " was added to " + group.getName());
            return true;
        }

        if (strings.length == 5
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("parent")
                && strings[3].equalsIgnoreCase("remove")
        ) {
            PermissionGroup group = PermissionAPI.getInstance().getPermissionUtil().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            if (!group.getSubGroups().contains(strings[4])) {
                System.out.println("The group " + strings[4] + " is not a parent of " + group.getName());
                return true;
            }

            group.getSubGroups().remove(strings[4]);
            PermissionAPI.getInstance().getPermissionUtil().updateGroup(group);
            System.out.println("Removed sub group " + strings[4] + " from " + group.getName());
            return true;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("parent")
        ) {
            PermissionGroup group = PermissionAPI.getInstance().getPermissionUtil().getGroup(strings[1]);
            if (group == null) {
                System.out.println("The group " + strings[1] + " does not exists");
                return true;
            }

            if (group.getSubGroups().isEmpty()) {
                System.out.println("The group " + group.getName() + " does not have any sub-groups");
                return true;
            }

            group.getSubGroups().clear();
            PermissionAPI.getInstance().getPermissionUtil().updateGroup(group);
            System.out.println("Cleared all sub group of " + group.getName());
            return true;
        }

        commandSource.sendMessages(HELP);
        return true;
    }

    private static TimeUnit parseUnitFromString(@Nonnull String s) {
        switch (s.toLowerCase()) {
            case "s":
                return TimeUnit.SECONDS;
            case "m":
                return TimeUnit.MINUTES;
            case "h":
                return TimeUnit.HOURS;
            case "d":
                return TimeUnit.DAYS;
            default: {
                return null;
            }
        }
    }
}
