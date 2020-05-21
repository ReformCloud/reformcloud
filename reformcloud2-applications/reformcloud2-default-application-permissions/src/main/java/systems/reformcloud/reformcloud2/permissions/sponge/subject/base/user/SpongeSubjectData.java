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
package systems.reformcloud.reformcloud2.permissions.sponge.subject.base.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectData;
import systems.reformcloud.reformcloud2.permissions.PermissionManagement;
import systems.reformcloud.reformcloud2.permissions.objects.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.objects.user.PermissionUser;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.AbstractSpongeSubjectData;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.util.SubjectGroupPermissionCalculator;

import java.util.*;

public class SpongeSubjectData extends AbstractSpongeSubjectData {

    private final UUID uniqueID;

    public SpongeSubjectData(@NotNull UUID user) {
        this.uniqueID = user;
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

    private Map<String, Boolean> getPermissions() {
        Map<String, Boolean> out = new HashMap<>();
        PermissionUser user = PermissionManagement.getInstance().loadUser(this.uniqueID);

        user.getPermissionNodes().forEach(e -> {
            if (!e.isValid()) {
                return;
            }

            out.put(e.getActualPermission(), e.isSet());
        });

        user.getGroups().forEach(e -> {
            if (!e.isValid()) {
                return;
            }

            PermissionGroup group = PermissionManagement.getInstance().getGroup(e.getGroupName());
            if (group == null) {
                return;
            }

            out.putAll(this.getPermissionsOf(group));
            group.getSubGroups().forEach(g -> {
                PermissionGroup sub = PermissionManagement.getInstance().getGroup(g);
                if (sub == null) {
                    return;
                }

                out.putAll(this.getPermissionsOf(sub));
            });
        });

        return out;
    }

    private Map<String, Boolean> getPermissionsOf(PermissionGroup group) {
        return SubjectGroupPermissionCalculator.getPermissionsOf(group);
    }
}
