/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.reformcloud2.permissions.util;

import org.jetbrains.annotations.NotNull;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.reformcloud.reformcloud2.executor.api.utility.MoreCollections;
import systems.reformcloud.reformcloud2.permissions.PermissionManagement;
import systems.reformcloud.reformcloud2.permissions.nodes.NodeGroup;
import systems.reformcloud.reformcloud2.permissions.nodes.PermissionNode;
import systems.reformcloud.reformcloud2.permissions.objects.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.objects.user.PermissionUser;
import systems.reformcloud.reformcloud2.permissions.packets.PacketHelper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public final class PermissionPluginUtil {

  private PermissionPluginUtil() {
    throw new UnsupportedOperationException();
  }

  public static void awaitConnection(@NotNull Runnable then) {
    PermissionManagement.setup();
    PacketHelper.addPacketHandler();
    then.run();
  }

  @NotNull
  public static Collection<PermissionNode> collectPermissionsOfUser(@NotNull PermissionUser permissionUser) {
    Collection<PermissionNode> permissionNodes = permissionUser.getPermissionNodes()
      .stream()
      .filter(PermissionNode::isValid)
      .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
    Collection<PermissionNode> groupPerms = permissionUser.getPerGroupPermissions().get(Embedded.getInstance().getCurrentProcessInformation().getProcessGroup().getName());
    if (groupPerms != null) {
      permissionNodes.addAll(groupPerms
        .stream()
        .filter(PermissionNode::isValid)
        .filter(node -> permissionNodes.stream().noneMatch(setNode -> setNode.getActualPermission().equals(node.getActualPermission())))
        .collect(Collectors.toList()));
    }

    Set<String> travelledGroups = new HashSet<>();
    permissionUser.getGroups().stream()
      .filter(NodeGroup::isValid)
      .map(group -> PermissionManagement.getInstance().getPermissionGroup(group.getGroupName()).orElse(null))
      .filter(Objects::nonNull)
      .forEach(group -> {
        // we have to recheck because we travel over each group which may has the group as sub-group
        if (!travelledGroups.contains(group.getName())) {
          collectPermissionsOfGroup(group, permissionNodes, travelledGroups);
        }
      });

    return permissionNodes;
  }

  @NotNull
  public static Collection<PermissionNode> collectPermissionsOfGroup(@NotNull PermissionGroup permissionGroup) {
    Collection<PermissionNode> permissionNodes = new CopyOnWriteArrayList<>();
    collectPermissionsOfGroup(permissionGroup, permissionNodes, new HashSet<>());
    return permissionNodes;
  }

  private static void collectPermissionsOfGroup(@NotNull PermissionGroup permissionGroup, @NotNull Collection<PermissionNode> permissionNodes, @NotNull Set<String> travelledGroups) {
    permissionNodes.addAll(permissionGroup.getPermissionNodes()
      .stream()
      .filter(PermissionNode::isValid)
      .filter(node -> permissionNodes.stream().noneMatch(setNode -> setNode.getActualPermission().equals(node.getActualPermission())))
      .collect(Collectors.toList()));
    permissionGroup.getSubGroups()
      .stream()
      .filter(MoreCollections.negate(travelledGroups::contains))
      .map(group -> PermissionManagement.getInstance().getPermissionGroup(group).orElse(null))
      .filter(Objects::nonNull)
      .forEach(group -> {
        // we have to recheck because we travel over each group which may has the group as sub-group
        if (!travelledGroups.contains(group.getName())) {
          collectPermissionsOfGroup(group, permissionNodes, travelledGroups);
        }
      });
  }
}
