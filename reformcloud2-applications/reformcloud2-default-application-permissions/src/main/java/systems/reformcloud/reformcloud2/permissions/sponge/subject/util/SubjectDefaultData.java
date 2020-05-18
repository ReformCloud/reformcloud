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
package systems.reformcloud.reformcloud2.permissions.sponge.subject.util;

import org.spongepowered.api.service.permission.Subject;
import systems.reformcloud.reformcloud2.permissions.sponge.collections.CollectionCatalog;
import systems.reformcloud.reformcloud2.permissions.sponge.service.SpongePermissionService;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.base.user.SpongeSubject;

import java.util.UUID;

public final class SubjectDefaultData {

    private static final UUID DEFAULT_UUID = UUID.fromString("a038e8a0-d294-45a5-86f0-bf44d316beec");
    public static final Subject DEFAULT = new SpongeSubject(DEFAULT_UUID, CollectionCatalog.USER_COLLECTION, SpongePermissionService.getInstance());

    private SubjectDefaultData() {
        throw new UnsupportedOperationException();
    }
}
