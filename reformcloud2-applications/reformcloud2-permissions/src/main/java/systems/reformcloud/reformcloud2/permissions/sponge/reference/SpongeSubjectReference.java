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
package systems.reformcloud.reformcloud2.permissions.sponge.reference;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectReference;

import java.util.concurrent.CompletableFuture;

public class SpongeSubjectReference implements SubjectReference {

  private final PermissionService service;
  private final String collection;
  private final String id;
  private Subject cache;

  public SpongeSubjectReference(
    @NotNull PermissionService service,
    @NotNull String collection,
    @NotNull String id
  ) {
    this.collection = collection;
    this.id = id;
    this.service = service;
  }

  @Override
  @NotNull
  public String getCollectionIdentifier() {
    return this.collection;
  }

  @Override
  @NotNull
  public String getSubjectIdentifier() {
    return this.id;
  }

  @Override
  @NotNull
  public CompletableFuture<Subject> resolve() {
    if (this.cache == null) {
      SubjectCollection subjectCollection = this.service.getCollection(this.collection).orElseThrow(() -> new IllegalArgumentException("Collection not loaded"));
      this.cache = subjectCollection.loadSubject(this.id).join();
    }

    return CompletableFuture.completedFuture(this.cache);
  }
}
