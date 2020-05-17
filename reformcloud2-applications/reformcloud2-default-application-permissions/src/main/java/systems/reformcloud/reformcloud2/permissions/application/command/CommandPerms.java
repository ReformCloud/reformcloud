/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.permissions.application.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.permissions.PermissionManagement;
import systems.reformcloud.reformcloud2.permissions.nodes.NodeGroup;
import systems.reformcloud.reformcloud2.permissions.nodes.PermissionNode;
import systems.reformcloud.reformcloud2.permissions.objects.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.objects.user.PermissionUser;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CommandPerms extends GlobalCommand {

    private static final String[] HELP = new String[]{
            "perms groups",
            "perms group [groupname]",
            "perms group [groupname] create",
            "perms group [groupname] create [default]",
            "perms group [groupname] delete",
            "perms group [groupname] clear",
            "perms group [groupname] clear [groups/permissions]",
            "perms group [groupname] setdefault [default]",
            "perms group [groupname] setpriority [priority]",
            "perms group [groupname] setprefix [prefix]",
            "perms group [groupname] setsuffix [suffix]",
            "perms group [groupname] setdisplay [display]",
            "perms group [groupname] setcolor [color]",
            "perms group [groupname] addgroup [groupname]",
            "perms group [groupname] delgroup [groupname]",
            "perms group [groupname] addperm [permission] [positive]",
            "perms group [groupname] addperm [permission] [positive] [timeout] [s/m/h/d/mo]",
            "perms group [groupname] addperm [processgroup] [permission] [positive]",
            "perms group [groupname] addperm [processgroup] [permission] [positive] [timeout] [s/m/h/d/mo]",
            "perms group [groupname] delperm [permission]",
            "perms group [groupname] delperm [processgroup] [permission]",
            "perms group [groupname] parent clear",
            " ",
            "perms user [user]",
            "perms user [user] delete",
            "perms user [user] clear",
            "perms user [user] clear [groups/permissions]",
            "perms user [user] setprefix [prefix]",
            "perms user [user] setsuffix [suffix]",
            "perms user [user] setdisplay [display]",
            "perms user [user] setcolor [color]",
            "perms user [user] addperm [permission] [positive]",
            "perms user [user] addperm [permission] [positive] [timeout] [s/m/h/d/mo]",
            "perms user [user] addperm [processgroup] [permission] [positive]",
            "perms user [user] addperm [processgroup] [permission] [positive] [timeout] [s/m/h/d/mo]",
            "perms user [user] delperm [permission]",
            "perms user [user] delperm [processgroup] [permission]",
            "perms user [user] addgroup [group]",
            "perms user [user] addgroup [group] [timeout] [s/m/h/d/mo]",
            "perms user [user] setgroup [group]",
            "perms user [user] setgroup [group] [timeout] [s/m/h/d/mo]",
            "perms user [user] delgroup [group]"
    };

    public CommandPerms() {
        super("perms", "reformcloud.command.perms", "The main perms command", "permissions", "cloudperms");
    }

    @Override
    public boolean handleCommand(@NotNull CommandSource commandSource, String @NotNull [] strings) {
        if (strings.length == 1 && strings[0].equalsIgnoreCase("groups")) {
            List<PermissionGroup> groups = new ArrayList<>(PermissionManagement.getInstance().getPermissionGroups());
            groups.sort(Comparator.comparingInt(PermissionGroup::getPriority));

            commandSource.sendMessage(String.format("Registered groups (%d): \n  - %s", groups.size(), String.join("\n  - ",
                    groups
                            .stream()
                            .map(e -> String.format("Name: %s | Priority: %d", e.getName(), e.getPriority()))
                            .toArray(String[]::new))
            ));
            return true;
        }

        if (strings.length >= 2 && strings[0].equalsIgnoreCase("user")) {
            this.handleUserCommand(commandSource, strings);
            return true;
        }

        if (strings.length >= 2 && strings[0].equalsIgnoreCase("group")) {
            this.handleGroupCommand(commandSource, strings);
            return true;
        }

        commandSource.sendMessages(HELP);
        return true;
    }

    private void handleUserCommand(@NotNull CommandSource source, @NotNull String[] strings) {
        Optional<PermissionUser> permissionUserOptional = PermissionManagement.getInstance().loadUser(strings[1]);
        if (!permissionUserOptional.isPresent()) {
            source.sendMessage("The permission user " + strings[1] + " is not present");
            return;
        }

        PermissionUser permissionUser = permissionUserOptional.get();
        if (strings.length == 2) {
            this.displayPermissionUser(source, permissionUser, strings[1]);
            return;
        }

        if (strings.length == 3) {
            if (strings[2].equalsIgnoreCase("delete")) {
                PermissionManagement.getInstance().deleteUser(permissionUser.getUniqueID());
                source.sendMessage("The permission user " + strings[1] + " was deleted");
                return;
            }

            if (strings[2].equalsIgnoreCase("clear")) {
                permissionUser.getPermissionNodes().clear();
                permissionUser.getGroups().clear();
                PermissionManagement.getInstance().updateUser(permissionUser);
                source.sendMessage("Cleared all permissions and groups of user " + strings[1]);
                return;
            }
        }

        if (strings.length == 4) {
            if (strings[2].equalsIgnoreCase("clear")) {
                if (strings[3].equalsIgnoreCase("groups")) {
                    permissionUser.getGroups().clear();
                    PermissionManagement.getInstance().assignDefaultGroups(permissionUser);
                    PermissionManagement.getInstance().updateUser(permissionUser);
                    source.sendMessage("Cleared all groups of user " + strings[1]);
                    return;
                }

                if (strings[3].equalsIgnoreCase("permissions") || strings[3].equalsIgnoreCase("perms")) {
                    permissionUser.getPermissionNodes().clear();
                    PermissionManagement.getInstance().updateUser(permissionUser);
                    source.sendMessage("Cleared all permissions of user " + strings[1]);
                    return;
                }
            }

            if (strings[2].equalsIgnoreCase("addgroup")) {
                Optional<PermissionGroup> permissionGroup = PermissionManagement.getInstance().getPermissionGroup(strings[3]);
                if (!permissionGroup.isPresent()) {
                    source.sendMessage("Unable to find permission group " + strings[3]);
                    return;
                }

                if (permissionUser.isInGroup(permissionGroup.get().getName())) {
                    source.sendMessage("The user " + strings[1] + " is already in the permission group " + strings[3]);
                    return;
                }

                permissionUser.getGroups().add(new NodeGroup(System.currentTimeMillis(), -1, permissionGroup.get().getName()));
                PermissionManagement.getInstance().updateUser(permissionUser);
                source.sendMessage("The user " + strings[1] + " is now in the permission group " + strings[3]);
                return;
            }

            if (strings[2].equalsIgnoreCase("setgroup")) {
                Optional<PermissionGroup> permissionGroup = PermissionManagement.getInstance().getPermissionGroup(strings[3]);
                if (!permissionGroup.isPresent()) {
                    source.sendMessage("Unable to find permission group " + strings[3]);
                    return;
                }

                permissionUser.getGroups().clear();
                PermissionManagement.getInstance().assignDefaultGroups(permissionUser);
                permissionUser.getGroups().add(new NodeGroup(System.currentTimeMillis(), -1, permissionGroup.get().getName()));
                PermissionManagement.getInstance().updateUser(permissionUser);
                source.sendMessage("The user " + strings[1] + " is now in the permission group " + strings[3]);
                return;
            }

            if (strings[2].equalsIgnoreCase("delgroup")) {
                if (!permissionUser.isInGroup(strings[3])) {
                    source.sendMessage("The user " + strings[1] + " is already in the permission group " + strings[3]);
                    return;
                }

                permissionUser.getGroups().removeIf(nodeGroup -> nodeGroup.getGroupName().equals(strings[3]));
                if (permissionUser.getGroups().isEmpty()) {
                    PermissionManagement.getInstance().assignDefaultGroups(permissionUser);
                }

                PermissionManagement.getInstance().updateUser(permissionUser);
                source.sendMessage("Removed permission group " + strings[3] + " from user " + strings[1]);
                return;
            }

            if (strings[2].equalsIgnoreCase("delperm")) {
                if (!permissionUser.getPermissionNodes().removeIf(node -> node.getActualPermission().equalsIgnoreCase(strings[3]))) {
                    source.sendMessage("The user " + strings[1] + " does not have the permission " + strings[3]);
                    return;
                }

                PermissionManagement.getInstance().updateUser(permissionUser);
                source.sendMessage("Removed the permission " + strings[3] + " from the user " + strings[1]);
                return;
            }

            if (strings[2].equalsIgnoreCase("setcolor")) {
                if (strings[3].length() > 2) {
                    source.sendMessage("You may only use colour codes or \"\" to reset the user's colour");
                    return;
                }

                String colour = strings[3];
                if (colour.equals("\"\"")) {
                    colour = null;
                }

                if (permissionUser.getColour().isPresent() && permissionUser.getColour().get().equals(colour)) {
                    source.sendMessage("The user " + strings[1] + " has already the colour " + colour);
                    return;
                }

                permissionUser.setColour(colour);
                PermissionManagement.getInstance().updateUser(permissionUser);
                source.sendMessage("The user " + strings[1] + " has " + (colour == null ? "no longer a colour" : "now the colour " + colour));
                return;
            }

            if (strings[2].equalsIgnoreCase("setprefix")) {
                String prefix = strings[3].replace("_", " ");
                if (prefix.equals("\"\"")) {
                    prefix = null;
                }

                if (permissionUser.getPrefix().isPresent() && permissionUser.getPrefix().get().equals(prefix)) {
                    source.sendMessage("The user " + strings[1] + " has already the prefix " + prefix);
                    return;
                }

                permissionUser.setPrefix(prefix);
                PermissionManagement.getInstance().updateUser(permissionUser);
                source.sendMessage("The user " + strings[1] + " has " + (prefix == null ? "no longer a prefix" : "now the prefix " + prefix));
                return;
            }

            if (strings[2].equalsIgnoreCase("setsuffix")) {
                String suffix = strings[3].replace("_", " ");
                if (suffix.equals("\"\"")) {
                    suffix = null;
                }

                if (permissionUser.getSuffix().isPresent() && permissionUser.getSuffix().get().equals(suffix)) {
                    source.sendMessage("The user " + strings[1] + " has already the suffix " + suffix);
                    return;
                }

                permissionUser.setSuffix(suffix);
                PermissionManagement.getInstance().updateUser(permissionUser);
                source.sendMessage("The user " + strings[1] + " has " + (suffix == null ? "no longer a suffix" : "now the suffix " + suffix));
                return;
            }

            if (strings[2].equalsIgnoreCase("setdisplay")) {
                String display = strings[3].replace("_", " ");
                if (display.equals("\"\"")) {
                    display = null;
                }

                if (permissionUser.getDisplay().isPresent() && permissionUser.getDisplay().get().equals(display)) {
                    source.sendMessage("The user " + strings[1] + " has already the display " + display);
                    return;
                }

                permissionUser.setDisplay(display);
                PermissionManagement.getInstance().updateUser(permissionUser);
                source.sendMessage("The user " + strings[1] + " has " + (display == null ? "no longer a display" : "now the display " + display));
                return;
            }
        }

        if (strings.length == 5) {
            if (strings[2].equalsIgnoreCase("delperm")) {
                Collection<PermissionNode> permissionNodes = permissionUser.getPerGroupPermissions().get(strings[3]);
                if (permissionNodes == null) {
                    source.sendMessage("The user " + strings[1] + " has no permissions on the process group " + strings[3]);
                    return;
                }

                if (!permissionNodes.removeIf(node -> node.getActualPermission().equalsIgnoreCase(strings[4]))) {
                    source.sendMessage("The permission " + strings[4] + " is not set for user " + strings[1] + " on process group " + strings[3]);
                    return;
                }

                PermissionManagement.getInstance().updateUser(permissionUser);
                source.sendMessage("The permission " + strings[4] + " was removed from the user " + strings[1] + " on the process group " + strings[3]);
                return;
            }

            if (strings[2].equalsIgnoreCase("addperm")) {
                if (permissionUser.getPermissionNodes().stream().anyMatch(node -> node.getActualPermission().equalsIgnoreCase(strings[3]))) {
                    source.sendMessage("The permission " + strings[3] + " is already set for user " + strings[1]);
                    return;
                }

                Boolean positive = CommonHelper.booleanFromString(strings[4]);
                if (positive == null) {
                    source.sendMessage("Please provide a boolean as 5. argument (true/false)");
                    return;
                }

                permissionUser.getPermissionNodes().add(PermissionNode.createNode(strings[3], -1, positive));
                PermissionManagement.getInstance().updateUser(permissionUser);
                source.sendMessage("The permission " + strings[3] + " is now set for the user " + strings[1] + " in context global");
                return;
            }
        }

        if (strings.length == 6) {
            if (strings[2].equalsIgnoreCase("addgroup")) {
                Optional<PermissionGroup> optionalPermissionGroup = PermissionManagement.getInstance().getPermissionGroup(strings[3]);
                if (!optionalPermissionGroup.isPresent()) {
                    source.sendMessage("The permission group " + strings[3] + " is not present");
                    return;
                }

                if (permissionUser.getGroups().stream().anyMatch(group -> group.getGroupName().equals(strings[3]))) {
                    source.sendMessage("The permission user " + strings[1] + " is already in the group " + strings[3]);
                    return;
                }

                Long requestedTimeout = CommonHelper.longFromString(strings[4]);
                if (requestedTimeout == null) {
                    source.sendMessage("Please provide a valid timeout time instead of " + strings[4]);
                    return;
                }

                if (requestedTimeout < -1) {
                    requestedTimeout = (long) -1;
                }

                Long timeout = parseTimeout(requestedTimeout, strings[5]);
                if (timeout == null) {
                    source.sendMessage("Please provide a valid timeout unit instaed of " + strings[5]);
                    return;
                }

                permissionUser.getGroups().add(new NodeGroup(System.currentTimeMillis(), timeout, optionalPermissionGroup.get().getName()));
                PermissionManagement.getInstance().updateUser(permissionUser);
                source.sendMessage("The user " + strings[1] + " is now in the group " + strings[3] + " "
                        + (timeout == -1 ? "lifetime" : "until " + CommonHelper.DATE_FORMAT.format(timeout)));
                return;
            }

            if (strings[2].equalsIgnoreCase("setgroup")) {
                Optional<PermissionGroup> optionalPermissionGroup = PermissionManagement.getInstance().getPermissionGroup(strings[3]);
                if (!optionalPermissionGroup.isPresent()) {
                    source.sendMessage("The permission group " + strings[3] + " is not present");
                    return;
                }

                Long requestedTimeout = CommonHelper.longFromString(strings[4]);
                if (requestedTimeout == null) {
                    source.sendMessage("Please provide a valid timeout time instead of " + strings[4]);
                    return;
                }

                if (requestedTimeout < -1) {
                    requestedTimeout = (long) -1;
                }

                Long timeout = parseTimeout(requestedTimeout, strings[5]);
                if (timeout == null) {
                    source.sendMessage("Please provide a valid timeout unit instaed of " + strings[5]);
                    return;
                }

                permissionUser.getGroups().clear();
                PermissionManagement.getInstance().assignDefaultGroups(permissionUser);
                permissionUser.getGroups().add(new NodeGroup(System.currentTimeMillis(), timeout, optionalPermissionGroup.get().getName()));
                PermissionManagement.getInstance().updateUser(permissionUser);
                source.sendMessage("The user " + strings[1] + " is now in the group " + strings[3] + " "
                        + (timeout == -1 ? "lifetime" : "until " + CommonHelper.DATE_FORMAT.format(timeout)));
                return;
            }

            if (strings[2].equalsIgnoreCase("addperm")) {
                Collection<PermissionNode> groupPerms = permissionUser.getPerGroupPermissions().get(strings[3]);
                if (groupPerms != null && groupPerms.stream().anyMatch(node -> node.getActualPermission().equalsIgnoreCase(strings[4]))) {
                    source.sendMessage("The permission " + strings[4] + " is already set for user " + strings[1] + " on the process group " + strings[3]);
                    return;
                }

                Boolean positive = CommonHelper.booleanFromString(strings[5]);
                if (positive == null) {
                    source.sendMessage("Please provide a boolean as 6. argument (true/false)");
                    return;
                }

                if (groupPerms == null) {
                    permissionUser.getPerGroupPermissions().put(strings[3], new ArrayList<>());
                }

                permissionUser.getPerGroupPermissions().get(strings[3]).add(PermissionNode.createNode(strings[4], -1, positive));
                PermissionManagement.getInstance().updateUser(permissionUser);
                source.sendMessage("The permission " + strings[4] + " is now set for the user " + strings[1] + " in context group:" + strings[3]);
                return;
            }
        }

        if (strings.length == 7 && strings[2].equalsIgnoreCase("addperm")) {
            if (permissionUser.getPermissionNodes().stream().anyMatch(node -> node.getActualPermission().equalsIgnoreCase(strings[3]))) {
                source.sendMessage("The permission " + strings[3] + " is already set for the user " + strings[1]);
                return;
            }

            Long requestedTimeout = CommonHelper.longFromString(strings[5]);
            if (requestedTimeout == null) {
                source.sendMessage("Please provide a valid timeout time instead of " + strings[5]);
                return;
            }

            if (requestedTimeout < -1) {
                requestedTimeout = (long) -1;
            }

            Long timeout = parseTimeout(requestedTimeout, strings[6]);
            if (timeout == null) {
                source.sendMessage("Please provide a valid timeout unit instaed of " + strings[6]);
                return;
            }

            Boolean positive = CommonHelper.booleanFromString(strings[4]);
            if (positive == null) {
                source.sendMessage("Please provide a boolean as 6. argument (true/false)");
                return;
            }

            permissionUser.getPermissionNodes().add(PermissionNode.createNode(strings[3], timeout, positive));
            PermissionManagement.getInstance().updateUser(permissionUser);
            source.sendMessage("The user " + strings[1] + " has now the permission " + strings[3] + " "
                    + (timeout == -1 ? "lifetime" : "until " + CommonHelper.DATE_FORMAT.format(timeout)));
            return;
        }

        if (strings.length == 8 && strings[2].equalsIgnoreCase("addperm")) {
            Collection<PermissionNode> permissionNodes = permissionUser.getPerGroupPermissions().get(strings[3]);
            if (permissionNodes != null && permissionNodes.stream().anyMatch(node -> node.getActualPermission().equalsIgnoreCase(strings[4]))) {
                source.sendMessage("The permission " + strings[4] + " is already set for the user " + strings[1] + " on the group " + strings[3]);
                return;
            }

            Long requestedTimeout = CommonHelper.longFromString(strings[6]);
            if (requestedTimeout == null) {
                source.sendMessage("Please provide a valid timeout time instead of " + strings[6]);
                return;
            }

            if (requestedTimeout < -1) {
                requestedTimeout = (long) -1;
            }

            Long timeout = parseTimeout(requestedTimeout, strings[7]);
            if (timeout == null) {
                source.sendMessage("Please provide a valid timeout unit instaed of " + strings[7]);
                return;
            }

            Boolean positive = CommonHelper.booleanFromString(strings[5]);
            if (positive == null) {
                source.sendMessage("Please provide a boolean as argument (true/false)");
                return;
            }

            if (permissionNodes == null) {
                permissionUser.getPerGroupPermissions().put(strings[3], new ArrayList<>());
            }

            permissionUser.getPerGroupPermissions().get(strings[3]).add(PermissionNode.createNode(strings[4], timeout, positive));
            PermissionManagement.getInstance().updateUser(permissionUser);
            source.sendMessage("The user " + strings[1] + " has now the permission " + strings[3] + " "
                    + (timeout == -1 ? "lifetime" : "until " + CommonHelper.DATE_FORMAT.format(timeout)) + " om the process group " + strings[3]);
            return;
        }

        source.sendMessages(HELP);
    }

    private void handleGroupCommand(@NotNull CommandSource source, @NotNull String[] strings) {
        Optional<PermissionGroup> optionalPermissionGroup = PermissionManagement.getInstance().getPermissionGroup(strings[1]);
        if ((strings.length == 3 || strings.length == 4) && strings[2].equalsIgnoreCase("create")) {
            if (optionalPermissionGroup.isPresent()) {
                source.sendMessage("The permission group " + strings[1] + " already exists");
                return;
            }

            Boolean defaultGroup = false;
            if (strings.length == 4) {
                defaultGroup = CommonHelper.booleanFromString(strings[3]);
            }

            if (defaultGroup == null) {
                source.sendMessage("Please provide a valid value as 4. Argument (true/false)");
                return;
            }

            PermissionManagement.getInstance().createPermissionGroup(new PermissionGroup(
                    new ArrayList<>(),
                    new HashMap<>(),
                    new ArrayList<>(),
                    strings[1],
                    0,
                    defaultGroup
            ));
            source.sendMessage("Successfully created new permission group " + strings[1]);
            return;
        }

        if (!optionalPermissionGroup.isPresent()) {
            source.sendMessage("The permission group " + strings[1] + " is not present");
            return;
        }

        PermissionGroup permissionGroup = optionalPermissionGroup.get();
        if (strings.length == 2) {
            this.displayPermissionGroup(source, permissionGroup);
            return;
        }

        if (strings.length == 3) {
            if (strings[2].equalsIgnoreCase("delete")) {
                PermissionManagement.getInstance().deleteGroup(permissionGroup.getName());
                source.sendMessage("Successfully deleted permission group " + strings[1]);
                return;
            }

            if (strings[2].equalsIgnoreCase("clear")) {
                permissionGroup.getPerGroupPermissions().clear();
                permissionGroup.getPermissionNodes().clear();
                permissionGroup.getSubGroups().clear();
                PermissionManagement.getInstance().updateGroup(permissionGroup);
                source.sendMessage("Successfully deleted all permissions and sub groups from group " + strings[1]);
                return;
            }
        }

        if (strings.length == 4) {
            if (strings[2].equalsIgnoreCase("clear")) {
                if (strings[3].equalsIgnoreCase("groups")) {
                    permissionGroup.getSubGroups().clear();
                    PermissionManagement.getInstance().updateGroup(permissionGroup);
                    source.sendMessage("Successfully removed all sub groups from group " + strings[1]);
                    return;
                }

                if (strings[3].equalsIgnoreCase("permissions") || strings[3].equalsIgnoreCase("perms")) {
                    permissionGroup.getPerGroupPermissions().clear();
                    permissionGroup.getPermissionNodes().clear();
                    PermissionManagement.getInstance().updateGroup(permissionGroup);
                    source.sendMessage("Successfully deleted all permissions from group " + strings[1]);
                    return;
                }
            }

            if (strings[2].equalsIgnoreCase("setdefault")) {
                Boolean defaultGroup = CommonHelper.booleanFromString(strings[3]);
                if (defaultGroup == null) {
                    source.sendMessage("Please provide a correct value (true/false)");
                    return;
                }

                if (defaultGroup && permissionGroup.isDefaultGroup()) {
                    source.sendMessage("The permission group " + strings[1] + " is already a default group");
                    return;
                }

                if (!defaultGroup && !permissionGroup.isDefaultGroup()) {
                    source.sendMessage("The permission group " + strings[1] + " is already a normal group");
                    return;
                }

                permissionGroup.setDefaultGroup(defaultGroup);
                PermissionManagement.getInstance().updateGroup(permissionGroup);
                source.sendMessage("The permission group " + strings[1] + " is now a " + (defaultGroup ? "default" : "normal") + " group");
                return;
            }

            if (strings[2].equalsIgnoreCase("setpriority")) {
                Integer priority = CommonHelper.fromString(strings[3]);
                if (priority == null) {
                    source.sendMessage("Please provide a valid int as priority");
                    return;
                }

                if (permissionGroup.getPriority() == priority) {
                    source.sendMessage("The permission group " + strings[1] + " has already the priority " + priority);
                    return;
                }

                permissionGroup.setPriority(priority);
                PermissionManagement.getInstance().updateGroup(permissionGroup);
                source.sendMessage("The permission group " + strings[1] + " has now the priority " + priority);
                return;
            }

            if (strings[2].equalsIgnoreCase("setcolor")) {
                String color = strings[3];
                if (color.length() > 2) {
                    source.sendMessage("Please use a valid chat color (for example &c)");
                    return;
                }

                if (color.equals("\"\"")) {
                    color = null;
                }

                if (permissionGroup.getColour().isPresent() && permissionGroup.getColour().get().equals(color)) {
                    source.sendMessage("The permission group " + strings[1] + " has already the color " + color);
                    return;
                }

                permissionGroup.setColour(color);
                PermissionManagement.getInstance().updateGroup(permissionGroup);
                source.sendMessage("The permission group " + strings[1] + " has " + (color == null ? "no longer a colour" : "now the color " + color));
                return;
            }

            if (strings[2].equalsIgnoreCase("setprefix")) {
                String prefix = strings[3].replace("_", " ");
                if (prefix.equals("\"\"")) {
                    prefix = null;
                }

                if (permissionGroup.getPrefix().isPresent() && permissionGroup.getPrefix().get().equals(prefix)) {
                    source.sendMessage("The permission group " + strings[1] + " has already the prefix " + prefix);
                    return;
                }

                permissionGroup.setPrefix(prefix);
                PermissionManagement.getInstance().updateGroup(permissionGroup);
                source.sendMessage("The permission group " + strings[1] + " has " + (prefix == null ? "no longer a prefix" : "now the prefix " + prefix));
                return;
            }

            if (strings[2].equalsIgnoreCase("setsuffix")) {
                String suffix = strings[3].replace("_", " ");
                if (suffix.equals("\"\"")) {
                    suffix = null;
                }

                if (permissionGroup.getSuffix().isPresent() && permissionGroup.getSuffix().get().equals(suffix)) {
                    source.sendMessage("The permission group " + strings[1] + " has already the suffix " + suffix);
                    return;
                }

                permissionGroup.setSuffix(suffix);
                PermissionManagement.getInstance().updateGroup(permissionGroup);
                source.sendMessage("The permission group " + strings[1] + " has " + (suffix == null ? "no longer a suffix" : "now the suffix " + suffix));
                return;
            }

            if (strings[2].equalsIgnoreCase("setdisplay")) {
                String display = strings[3].replace("_", " ");
                if (display.equals("\"\"")) {
                    display = null;
                }

                if (permissionGroup.getDisplay().isPresent() && permissionGroup.getDisplay().get().equals(display)) {
                    source.sendMessage("The permission group " + strings[1] + " has already the display " + display);
                    return;
                }

                permissionGroup.setDisplay(display);
                PermissionManagement.getInstance().updateGroup(permissionGroup);
                source.sendMessage("The permission group " + strings[1] + " has " + (display == null ? "no longer a display" : "now the display " + display));
                return;
            }

            if (strings[2].equalsIgnoreCase("delperm")) {
                if (!permissionGroup.getPermissionNodes().removeIf(node -> node.getActualPermission().equalsIgnoreCase(strings[3]))) {
                    source.sendMessage("The permission " + strings[3] + " is not set for the group " + strings[1]);
                    return;
                }

                PermissionManagement.getInstance().updateGroup(permissionGroup);
                source.sendMessage("The permission " + strings[3] + " was successfully removed from the group " + strings[1]);
                return;
            }

            if (strings[2].equalsIgnoreCase("addgroup")) {
                if (permissionGroup.getSubGroups().stream().anyMatch(group -> group.equalsIgnoreCase(strings[3]))) {
                    source.sendMessage("The permission group " + strings[3] + " is already a sub group of " + strings[1]);
                    return;
                }

                Optional<PermissionGroup> other = PermissionManagement.getInstance().getPermissionGroup(strings[3]);
                if (!other.isPresent()) {
                    source.sendMessage("The permission group " + strings[3] + " does not exists");
                    return;
                }

                permissionGroup.getSubGroups().add(other.get().getName());
                PermissionManagement.getInstance().updateGroup(permissionGroup);
                source.sendMessage("The permission group " + other.get().getName() + " is now a sub group of " + strings[1]);
                return;
            }

            if (strings[2].equalsIgnoreCase("delgroup")) {
                if (!permissionGroup.getSubGroups().removeIf(group -> group.equalsIgnoreCase(strings[3]))) {
                    source.sendMessage("The permission group " + strings[3] + " is not a sub group of " + strings[1]);
                    return;
                }

                PermissionManagement.getInstance().updateGroup(permissionGroup);
                source.sendMessage("The permission group " + strings[3] + " is no longer a sub group of " + strings[1]);
                return;
            }
        }

        if (strings.length == 5) {
            if (strings[2].equalsIgnoreCase("addperm")) {
                if (permissionGroup.getPermissionNodes().stream().anyMatch(node -> node.getActualPermission().equalsIgnoreCase(strings[3]))) {
                    source.sendMessage("The permission " + strings[3] + " is already set for the group " + strings[1]);
                    return;
                }

                Boolean positive = CommonHelper.booleanFromString(strings[4]);
                if (positive == null) {
                    source.sendMessage("Please provide a valid boolean as 5. argument (true/false)");
                    return;
                }

                permissionGroup.getPermissionNodes().add(PermissionNode.createNode(strings[3], -1, positive));
                PermissionManagement.getInstance().updateGroup(permissionGroup);
                source.sendMessage("The permission group " + strings[1] + " has now the permission " + strings[3]);
                return;
            }

            if (strings[2].equalsIgnoreCase("delperm")) {
                Collection<PermissionNode> nodes = permissionGroup.getPerGroupPermissions().get(strings[3]);
                if (nodes == null || !nodes.removeIf(node -> node.getActualPermission().equalsIgnoreCase(strings[4]))) {
                    source.sendMessage("The permission " + strings[4] + " is not set for the group " + strings[1]);
                    return;
                }

                PermissionManagement.getInstance().updateGroup(permissionGroup);
                source.sendMessage("The permission " + strings[4] + " was removed from the group " + strings[1]);
                return;
            }
        }

        if (strings.length == 6 && strings[2].equalsIgnoreCase("addperm")) {
            Collection<PermissionNode> nodes = permissionGroup.getPerGroupPermissions().get(strings[3]);
            if (nodes != null && nodes.stream().anyMatch(node -> node.getActualPermission().equalsIgnoreCase(strings[4]))) {
                source.sendMessage("The permission " + strings[4] + " is already set for the group " + strings[1] + " in the context group:" + strings[3]);
                return;
            }

            Boolean positive = CommonHelper.booleanFromString(strings[5]);
            if (positive == null) {
                source.sendMessage("Please provide a valid boolean as 6. argument (true/false)");
                return;
            }

            if (nodes == null) {
                permissionGroup.getPerGroupPermissions().put(strings[3], new ArrayList<>());
            }

            permissionGroup.getPerGroupPermissions().get(strings[3]).add(PermissionNode.createNode(strings[4], -1, positive));
            PermissionManagement.getInstance().updateGroup(permissionGroup);
            source.sendMessage("The permission group " + strings[1] + " has now the permission " + strings[4] + " in the context group:" + strings[3]);
            return;
        }

        if (strings.length == 7 && strings[2].equalsIgnoreCase("addperm")) {
            if (permissionGroup.getPermissionNodes().stream().anyMatch(node -> node.getActualPermission().equalsIgnoreCase(strings[3]))) {
                source.sendMessage("The permission " + strings[3] + " is already set for the group " + strings[1] + " in the context global");
                return;
            }

            Boolean positive = CommonHelper.booleanFromString(strings[4]);
            if (positive == null) {
                source.sendMessage("Please provide a valid boolean as 5. argument (true/false)");
                return;
            }

            Long requestedTimeout = CommonHelper.longFromString(strings[5]);
            if (requestedTimeout == null) {
                source.sendMessage("Please provide a valid timeout instead of " + strings[5]);
                return;
            }

            if (requestedTimeout < -1) {
                requestedTimeout = (long) -1;
            }

            Long timeout = parseTimeout(requestedTimeout, strings[6]);
            if (timeout == null) {
                source.sendMessage("Please provide a valid timeout unit " + strings[6]);
                return;
            }

            permissionGroup.getPermissionNodes().add(PermissionNode.createNode(strings[3], timeout, positive));
            PermissionManagement.getInstance().updateGroup(permissionGroup);
            source.sendMessage("The group " + strings[1] + " has now the permission " + strings[3] + " in context global");
            return;
        }

        if (strings.length == 8 && strings[2].equalsIgnoreCase("addperm")) {
            Collection<PermissionNode> nodes = permissionGroup.getPerGroupPermissions().get(strings[3]);
            if (nodes != null && nodes.stream().anyMatch(node -> node.getActualPermission().equalsIgnoreCase(strings[4]))) {
                source.sendMessage("The permission " + strings[4] + " is already set for the group " + strings[1] + " in the context group:" + strings[3]);
                return;
            }

            Boolean positive = CommonHelper.booleanFromString(strings[5]);
            if (positive == null) {
                source.sendMessage("Please provide a valid boolean as 6. argument (true/false)");
                return;
            }

            Long requestedTimeout = CommonHelper.longFromString(strings[6]);
            if (requestedTimeout == null) {
                source.sendMessage("Please provide a valid timeout instead of " + strings[6]);
                return;
            }

            if (requestedTimeout < -1) {
                requestedTimeout = (long) -1;
            }

            Long timeout = parseTimeout(requestedTimeout, strings[7]);
            if (timeout == null) {
                source.sendMessage("Please provide a valid timeout unit " + strings[7]);
                return;
            }

            if (nodes == null) {
                permissionGroup.getPerGroupPermissions().put(strings[3], new ArrayList<>());
            }

            permissionGroup.getPerGroupPermissions().get(strings[3]).add(PermissionNode.createNode(strings[4], timeout, positive));
            PermissionManagement.getInstance().updateGroup(permissionGroup);
            source.sendMessage("The group " + strings[1] + " has now the permission " + strings[4] + " in the context group:" + strings[3]);
            return;
        }

        source.sendRawMessages(HELP);
    }

    private void displayPermissionGroup(@NotNull CommandSource source, @NotNull PermissionGroup permissionGroup) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Name     - ").append(permissionGroup.getName()).append("\n");
        stringBuilder.append("Type     - ").append(permissionGroup.isDefaultGroup() ? "default group" : "normal group").append("\n");
        stringBuilder.append("Prefix   - ").append(permissionGroup.getPrefix().orElse("none")).append("\n");
        stringBuilder.append("Suffix   - ").append(permissionGroup.getSuffix().orElse("none")).append("\n");
        stringBuilder.append("Display  - ").append(permissionGroup.getDisplay().orElse("none")).append("\n");
        stringBuilder.append("Colour   - ").append(permissionGroup.getColour().orElse("none")).append("\n");
        stringBuilder.append("Priority - ").append(permissionGroup.getPriority()).append("\n");
        for (String s : permissionGroup.getExtra().toPrettyString().split("\n")) {
            stringBuilder.append("Extra    - ").append(s).append("\n");
        }

        stringBuilder.append(" Sub-Groups (").append(permissionGroup.getSubGroups().size()).append("):").append("\n");
        for (String subGroup : permissionGroup.getSubGroups()) {
            stringBuilder.append("  ").append(subGroup).append("\n");
        }

        stringBuilder.append("\n").append(" Permissions (").append(permissionGroup.getPermissionNodes().size()).append("):").append("\n");
        for (PermissionNode permissionNode : permissionGroup.getPermissionNodes()) {
            stringBuilder.append(formatPermissionNode(permissionNode));
        }

        stringBuilder.append("\n").append(" Per-Group-Permissions (").append(permissionGroup.getPerGroupPermissions().size()).append("):").append("\n");
        for (Map.Entry<String, Collection<PermissionNode>> stringCollectionEntry : permissionGroup.getPerGroupPermissions().entrySet()) {
            stringBuilder.append("  Per-Group-Permissions on ").append(stringCollectionEntry.getKey()).append(" (").append(stringCollectionEntry.getValue().size()).append("):").append("\n");
            for (PermissionNode node : stringCollectionEntry.getValue()) {
                stringBuilder.append(formatPermissionNode(node));
            }
        }

        source.sendMessages(stringBuilder.toString().split("\n"));
    }

    private void displayPermissionUser(@NotNull CommandSource source, @NotNull PermissionUser permissionUser, @NotNull String userName) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Name          - ").append(userName).append("\n");
        stringBuilder.append("UniqueId      - ").append(permissionUser.getUniqueID()).append("\n");
        stringBuilder.append("Prefix        - ").append(permissionUser.getPrefix().orElse("none")).append("\n");
        stringBuilder.append("Suffix        - ").append(permissionUser.getSuffix().orElse("none")).append("\n");
        stringBuilder.append("Display       - ").append(permissionUser.getDisplay().orElse("none")).append("\n");
        stringBuilder.append("Colour        - ").append(permissionUser.getColour().orElse("none")).append("\n");
        stringBuilder.append("Highest Group - ").append(permissionUser.getHighestPermissionGroup().map(PermissionGroup::getName).orElse("none")).append("\n");
        for (String s : permissionUser.getExtra().toPrettyString().split("\n")) {
            stringBuilder.append("Extra         - ").append(s).append("\n");
        }

        stringBuilder.append(" Groups (").append(permissionUser.getGroups().size()).append("):").append("\n");
        for (NodeGroup group : permissionUser.getGroups()) {
            if (!group.isValid()) {
                continue;
            }

            stringBuilder.append("  ").append(group.getGroupName()).append(" | Since: ").append(CommonHelper.DATE_FORMAT.format(group.getAddTime())).append(" | Until: ");
            if (group.getTimeout() == -1) {
                stringBuilder.append("lifetime");
            } else {
                stringBuilder.append(CommonHelper.DATE_FORMAT.format(group.getTimeout()));
            }

            stringBuilder.append("\n");
        }

        stringBuilder.append("\n").append(" Permissions (").append(permissionUser.getPermissionNodes().size()).append("):").append("\n");
        for (PermissionNode permissionNode : permissionUser.getPermissionNodes()) {
            stringBuilder.append(formatPermissionNode(permissionNode));
        }

        stringBuilder.append("\n").append(" Group-Permissions (").append(permissionUser.getPerGroupPermissions().size()).append("):").append("\n");
        for (Map.Entry<String, Collection<PermissionNode>> stringCollectionEntry : permissionUser.getPerGroupPermissions().entrySet()) {
            stringBuilder.append("  Group-Permissions on ").append(stringCollectionEntry.getKey()).append(" (").append(stringCollectionEntry.getValue().size()).append("):").append("\n");
            for (PermissionNode node : stringCollectionEntry.getValue()) {
                stringBuilder.append(formatPermissionNode(node));
            }
        }

        source.sendMessages(stringBuilder.toString().split("\n"));
    }

    @NotNull
    private static String formatPermissionNode(@NotNull PermissionNode node) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("  ").append(node.getActualPermission()).append(" | Since: ").append(CommonHelper.DATE_FORMAT.format(node.getAddTime())).append(" | Until: ");
        if (node.getTimeout() == -1) {
            stringBuilder.append("lifetime");
        } else {
            stringBuilder.append(CommonHelper.DATE_FORMAT.format(node.getTimeout()));
        }

        return stringBuilder.append("\n").toString();
    }

    @Nullable
    private static Long parseTimeout(long givenTime, @NotNull String requestedTimeUnit) {
        if (givenTime == -1) {
            return givenTime;
        }

        switch (requestedTimeUnit.toLowerCase()) {
            case "s":
            case "seconds":
                return System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(givenTime);
            case "m":
            case "minutes":
                return System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(givenTime);
            case "h":
            case "hours":
                return System.currentTimeMillis() + TimeUnit.HOURS.toMillis(givenTime);
            case "d":
            case "days":
                return System.currentTimeMillis() + TimeUnit.DAYS.toMillis(givenTime);
            case "mo":
            case "months":
                return System.currentTimeMillis() + 30 * givenTime * TimeUnit.DAYS.toMillis(1);
            default:
                return null;
        }
    }
}
