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
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectCollection;
import systems.reformcloud.reformcloud2.permissions.PermissionManagement;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.impl.AbstractUserSpongeSubject;

import java.util.UUID;

public class SpongeSubject extends AbstractUserSpongeSubject {

    private final UUID uniqueUserID;
    private final SubjectCollection source;
    private final PermissionService service;

    public SpongeSubject(@NotNull UUID user, @NotNull SubjectCollection source, @NotNull PermissionService service) {
        super(user);
        this.uniqueUserID = user;
        this.source = source;
        this.service = service;
    }

    @Override
    protected PermissionService service() {
        return this.service;
    }

    @Override
    protected boolean has(String permission) {
        return PermissionManagement.getInstance().loadUser(this.uniqueUserID).hasPermission(permission);
    }

    @Override
    @NotNull
    public SubjectCollection getContainingCollection() {
        return this.source;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return this.uniqueUserID.toString();
    }
}
