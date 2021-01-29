/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
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
package systems.reformcloud.permissions.sponge.subject.base.group;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectData;
import systems.reformcloud.permissions.PermissionManagement;
import systems.reformcloud.permissions.nodes.PermissionNode;
import systems.reformcloud.permissions.util.PermissionPluginUtil;
import systems.reformcloud.permissions.sponge.subject.AbstractSpongeSubjectData;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupSubjectData extends AbstractSpongeSubjectData {

  private final String group;

  public GroupSubjectData(@NotNull String group) {
    this.group = group;
  }

  @Override
  @NotNull
  public Map<Set<Context>, Map<String, Boolean>> getAllPermissions() {
    return Collections.singletonMap(SubjectData.GLOBAL_CONTEXT, this.getPermissions());
  }

  @Override
  @NotNull
  public Map<String, Boolean> getPermissions(@Nullable Set<Context> contexts) {
    return this.getPermissions();
  }

  @NotNull
  private Map<String, Boolean> getPermissions() {
    return PermissionManagement.getInstance().getPermissionGroup(this.group)
      .map(PermissionPluginUtil::collectPermissionsOfGroup)
      .orElseGet(Collections::emptyList)
      .stream()
      .collect(Collectors.toMap(PermissionNode::getActualPermission, PermissionNode::isSet));
  }
}
