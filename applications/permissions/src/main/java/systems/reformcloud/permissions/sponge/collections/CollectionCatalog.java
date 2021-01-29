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
package systems.reformcloud.permissions.sponge.collections;

import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectCollection;
import systems.reformcloud.permissions.sponge.collections.base.GroupSubjectCollection;
import systems.reformcloud.permissions.sponge.collections.base.SystemSubjectCollection;
import systems.reformcloud.permissions.sponge.collections.base.UserCollection;
import systems.reformcloud.permissions.sponge.collections.factory.FactoryCollection;
import systems.reformcloud.permissions.sponge.service.SpongePermissionService;

public final class CollectionCatalog {

  public static final SubjectCollection FACTORY_COLLECTION = new FactoryCollection(SpongePermissionService.getInstance());
  public static final SubjectCollection GROUP_COLLECTION = new GroupSubjectCollection(SpongePermissionService.getInstance());
  public static final SubjectCollection USER_COLLECTION = new UserCollection(SpongePermissionService.getInstance());
  public static final SubjectCollection COMMAND_BLOCK_COLLECTION = new SystemSubjectCollection(PermissionService.SUBJECTS_COMMAND_BLOCK, SpongePermissionService.getInstance());
  public static final SubjectCollection SYSTEM_COLLECTION = new SystemSubjectCollection(PermissionService.SUBJECTS_SYSTEM, SpongePermissionService.getInstance());

  private CollectionCatalog() {
    throw new UnsupportedOperationException();
  }
}
