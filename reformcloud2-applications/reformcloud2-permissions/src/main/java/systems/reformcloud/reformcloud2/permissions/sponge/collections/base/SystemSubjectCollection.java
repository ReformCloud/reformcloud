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
package systems.reformcloud.reformcloud2.permissions.sponge.collections.base;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import systems.reformcloud.reformcloud2.permissions.sponge.collections.DefaultSubjectCollection;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.base.system.SystemSubject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class SystemSubjectCollection extends DefaultSubjectCollection {

  public SystemSubjectCollection(String type, PermissionService service) {
    super(type, service);
  }

  @NotNull
  @Override
  protected Subject load(String id) {
    return new SystemSubject(id, this.service, this);
  }

  @Override
  @NotNull
  public CompletableFuture<Boolean> hasSubject(@NotNull String identifier) {
    return CompletableFuture.completedFuture(true);
  }

  @Override
  @NotNull
  public Collection<Subject> getLoadedSubjects() {
    return new ArrayList<>();
  }
}
