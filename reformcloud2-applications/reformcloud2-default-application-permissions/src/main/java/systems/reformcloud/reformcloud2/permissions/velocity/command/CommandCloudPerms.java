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
package systems.reformcloud.reformcloud2.permissions.velocity.command;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;
import systems.reformcloud.reformcloud2.permissions.PermissionManagement;
import systems.reformcloud.reformcloud2.permissions.internal.UUIDFetcher;
import systems.reformcloud.reformcloud2.permissions.nodes.NodeGroup;
import systems.reformcloud.reformcloud2.permissions.nodes.PermissionNode;
import systems.reformcloud.reformcloud2.permissions.objects.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.objects.user.PermissionUser;
import systems.reformcloud.reformcloud2.permissions.util.InternalTimeUnit;
import systems.reformcloud.reformcloud2.permissions.velocity.VelocityPermissionPlugin;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CommandCloudPerms implements Command {

    @Override
    public void execute(CommandSource commandSender, @NotNull String[] strings) {
        if (strings.length == 4
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("addgroup")
        ) {
            UUID uniqueID = getUniqueIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSender.sendMessage(TextComponent.of("§cThe uniqueID is unknown"));
                return;
            }

            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[3]);
            if (group == null) {
                commandSender.sendMessage(TextComponent.of("§cThe group " + strings[3] + " does not exists"));
                return;
            }

            PermissionUser user = PermissionManagement.getInstance().loadUser(uniqueID);
            if (Streams.filterToReference(user.getGroups(), e -> e.getGroupName().equals(strings[3]) && e.isValid()).isPresent()) {
                commandSender.sendMessage(TextComponent.of("§cThe user " + strings[1] + " is already in group " + strings[3]));
                return;
            }

            user.getGroups().add(new NodeGroup(
                    System.currentTimeMillis(),
                    -1,
                    group.getName()
            ));
            PermissionManagement.getInstance().updateUser(user);
            commandSender.sendMessage(TextComponent.of("§aSuccessfully §7added user " + strings[1] + " to group " + strings[3]));
            return;
        }

        if (strings.length == 6
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("addgroup")
        ) {
            UUID uniqueID = getUniqueIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSender.sendMessage(TextComponent.of("§cThe uniqueID is unknown"));
                return;
            }

            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[3]);
            if (group == null) {
                commandSender.sendMessage(TextComponent.of("§cThe group " + strings[3] + " does not exists"));
                return;
            }

            PermissionUser user = PermissionManagement.getInstance().loadUser(uniqueID);
            if (Streams.filterToReference(user.getGroups(), e -> e.getGroupName().equals(strings[3]) && e.isValid()).isPresent()) {
                commandSender.sendMessage(TextComponent.of("§cThe user " + strings[1] + " is already in group " + strings[3]));
                return;
            }

            Long givenTimeOut = CommonHelper.longFromString(strings[4]);
            if (givenTimeOut == null) {
                commandSender.sendMessage(TextComponent.of("§cThe timout time is not valid"));
                return;
            }

            long timeOut = System.currentTimeMillis()
                    + InternalTimeUnit.convert(parseUnitFromString(strings[5]), givenTimeOut);
            user.getGroups().add(new NodeGroup(
                    System.currentTimeMillis(),
                    timeOut,
                    group.getName()
            ));
            commandSender.sendMessage(TextComponent.of("§aSuccessfully §7added user " + strings[1] + " to group " + strings[3]));
            return;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("delgroup")
        ) {
            UUID uniqueID = getUniqueIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSender.sendMessage(TextComponent.of("§cThe uniqueID is unknown"));
                return;
            }

            PermissionUser user = PermissionManagement.getInstance().loadUser(uniqueID);
            NodeGroup filter = Streams.filter(user.getGroups(), e -> e.getGroupName().equals(strings[3]));
            if (filter == null) {
                commandSender.sendMessage(TextComponent.of("§cThe user " + strings[1] + " is not in group " + strings[3]));
                return;
            }

            user.getGroups().remove(filter);
            PermissionManagement.getInstance().updateUser(user);
            commandSender.sendMessage(TextComponent.of("§aSuccessfully §7removed group " + strings[3] + " from user " + strings[1]));
            return;
        }

        if (strings.length == 5
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("addperm")
        ) {
            UUID uniqueID = getUniqueIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSender.sendMessage(TextComponent.of("§cThe uniqueID is unknown"));
                return;
            }

            PermissionUser user = PermissionManagement.getInstance().loadUser(uniqueID);
            if (Streams.filterToReference(user.getPermissionNodes(),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3])).isPresent()) {
                commandSender.sendMessage(TextComponent.of("§cThe permission " + strings[3] + " is already set"));
                return;
            }

            Boolean set = CommonHelper.booleanFromString(strings[4]);
            if (set == null) {
                commandSender.sendMessage(TextComponent.of("§cThe permission may not be set correctly. Please recheck (use true/false as set argument)"));
                return;
            }

            user.getPermissionNodes().add(new PermissionNode(
                    System.currentTimeMillis(),
                    -1,
                    set,
                    strings[3]
            ));
            PermissionManagement.getInstance().updateUser(user);
            commandSender.sendMessage(TextComponent.of("The permission " + strings[3] + " was added to the user " + strings[1] + " with value " + set));
            return;
        }

        if (strings.length == 7
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("addperm")
        ) {
            UUID uniqueID = getUniqueIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSender.sendMessage(TextComponent.of("§cThe uniqueID is unknown"));
                return;
            }

            PermissionUser user = PermissionManagement.getInstance().loadUser(uniqueID);
            if (Streams.filterToReference(user.getPermissionNodes(),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3])).isPresent()) {
                commandSender.sendMessage(TextComponent.of("§cThe permission " + strings[3] + " is already set"));
                return;
            }

            Boolean set = CommonHelper.booleanFromString(strings[4]);
            if (set == null) {
                commandSender.sendMessage(TextComponent.of("§cThe permission may not be set correctly. Please recheck (use true/false as set argument)"));
                return;
            }

            Long givenTimeOut = CommonHelper.longFromString(strings[5]);
            if (givenTimeOut == null) {
                commandSender.sendMessage(TextComponent.of("§cThe timout time is not valid"));
                return;
            }

            long timeOut = System.currentTimeMillis()
                    + InternalTimeUnit.convert(parseUnitFromString(strings[6]), givenTimeOut);
            user.getPermissionNodes().add(new PermissionNode(
                    System.currentTimeMillis(),
                    timeOut,
                    set,
                    strings[3]
            ));
            PermissionManagement.getInstance().updateUser(user);
            commandSender.sendMessage(TextComponent.of("The permission " + strings[3] + " was added to the user " + strings[1] + " with value " + set));
            return;
        }

        if (strings.length == 4
                && strings[0].equalsIgnoreCase("user")
                && strings[2].equalsIgnoreCase("delperm")
        ) {
            UUID uniqueID = getUniqueIDFromName(strings[1]);
            if (uniqueID == null) {
                commandSender.sendMessage(TextComponent.of("§cThe uniqueID is unknown"));
                return;
            }

            PermissionUser user = PermissionManagement.getInstance().loadUser(uniqueID);
            ReferencedOptional<PermissionNode> perm = Streams.filterToReference(user.getPermissionNodes(),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3]));
            if (!perm.isPresent()) {
                commandSender.sendMessage(TextComponent.of("§cThe permission " + strings[3] + " is not set"));
                return;
            }

            user.getPermissionNodes().remove(perm.get());
            PermissionManagement.getInstance().updateUser(user);
            commandSender.sendMessage(TextComponent.of("Removed permission " + strings[3] + " from user " + strings[1]));
            return;
        }

        //"perms group [groupname] addperm [permission] [set] [timeout] [s/m/h/d/mo]",
        if (strings.length == 5
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("addperm")
        ) {
            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[1]);
            if (group == null) {
                commandSender.sendMessage(TextComponent.of("§cThe group " + strings[1] + " does not exists"));
                return;
            }

            if (Streams.filterToReference(group.getPermissionNodes(),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3])).isPresent()) {
                commandSender.sendMessage(TextComponent.of("§cThe permission " + strings[3] + " is already set for group " + strings[3]));
                return;
            }

            Boolean set = CommonHelper.booleanFromString(strings[4]);
            if (set == null) {
                commandSender.sendMessage(TextComponent.of("§cThe permission may not be set correctly. Please recheck (use true/false as set argument)"));
                return;
            }

            group.getPermissionNodes().add(new PermissionNode(
                    System.currentTimeMillis(),
                    -1,
                    set,
                    strings[3]
            ));
            PermissionManagement.getInstance().updateGroup(group);
            commandSender.sendMessage(TextComponent.of("§7The permission " + strings[3] + " was added to group " + group.getName()));
            return;
        }

        if (strings.length == 7
                && strings[0].equalsIgnoreCase("group")
                && strings[2].equalsIgnoreCase("addperm")
        ) {
            PermissionGroup group = PermissionManagement.getInstance().getGroup(strings[1]);
            if (group == null) {
                commandSender.sendMessage(TextComponent.of("§cThe group " + strings[1] + " does not exists"));
                return;
            }

            if (Streams.filterToReference(group.getPermissionNodes(),
                    e -> e.getActualPermission().equalsIgnoreCase(strings[3])).isPresent()) {
                commandSender.sendMessage(TextComponent.of("§cThe permission " + strings[3] + " is already set for group " + strings[3]));
                return;
            }

            Boolean set = CommonHelper.booleanFromString(strings[4]);
            if (set == null) {
                commandSender.sendMessage(TextComponent.of("§cThe permission may not be set correctly. Please recheck (use true/false as set argument)"));
                return;
            }

            Long givenTimeOut = CommonHelper.longFromString(strings[5]);
            if (givenTimeOut == null) {
                commandSender.sendMessage(TextComponent.of("§cThe timout time is not valid"));
                return;
            }

            long timeOut = System.currentTimeMillis()
                    + InternalTimeUnit.convert(parseUnitFromString(strings[6]), givenTimeOut);
            group.getPermissionNodes().add(new PermissionNode(
                    System.currentTimeMillis(),
                    timeOut,
                    set,
                    strings[3]
            ));
            PermissionManagement.getInstance().updateGroup(group);
            commandSender.sendMessage(TextComponent.of("§7The permission " + strings[3] + " was added to group " + group.getName()));
            return;
        }

        commandSender.sendMessage(TextComponent.of("§7/perms user [user] addperm [permission] [set]"));
        commandSender.sendMessage(TextComponent.of("§7/perms user [user] addperm [permission] [set] [timeout] [s/m/h/d/mo]"));
        commandSender.sendMessage(TextComponent.of("§7/perms user [user] delperm [permission]"));
        commandSender.sendMessage(TextComponent.of("§7/perms user [user] addgroup [group]"));
        commandSender.sendMessage(TextComponent.of("§7/perms user [user] addgroup [group] [timeout] [s/m/h/d/mo]"));
        commandSender.sendMessage(TextComponent.of("§7/perms user [user] delgroup [group]"));
        commandSender.sendMessage(TextComponent.of("§7/perms group [groupname] addperm [permission] [set]"));
        commandSender.sendMessage(TextComponent.of("§7/perms group [groupname] addperm [permission] [set] [timeout] [s/m/h/d/mo]"));
        commandSender.sendMessage(TextComponent.of("§7/perms group [groupname] delperm [permission]"));
    }

    @Override
    public boolean hasPermission(CommandSource source, @NotNull String[] args) {
        return source.hasPermission("reformcloud.command.cloudperms");
    }

    private static UUID getUniqueIDFromName(String name) {
        Optional<Player> player = VelocityPermissionPlugin.getProxy().getPlayer(name);
        return player.isPresent() ? player.get().getUniqueId() : UUIDFetcher.getUUIDFromName(name);
    }

    private static TimeUnit parseUnitFromString(@NotNull String s) {
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
