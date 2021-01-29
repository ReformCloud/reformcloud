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
package systems.reformcloud.reformcloud2.permissions.sponge.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectReference;
import systems.reformcloud.reformcloud2.permissions.sponge.collections.CollectionCatalog;
import systems.reformcloud.reformcloud2.permissions.sponge.description.SpongePermissionDescriptionBuilder;
import systems.reformcloud.reformcloud2.permissions.sponge.reference.SpongeSubjectReference;
import systems.reformcloud.reformcloud2.permissions.sponge.subject.util.SubjectDefaultData;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class SpongePermissionService implements PermissionService {

  public static final Map<String, PermissionDescription> DESCRIPTIONS = new ConcurrentHashMap<>();
  private static final Map<String, SubjectCollection> LOADED = new HashMap<>();

  // ======
  private static SpongePermissionService instance;

  // ======

  static {
    LOADED.put(SUBJECTS_COMMAND_BLOCK, CollectionCatalog.COMMAND_BLOCK_COLLECTION);
    LOADED.put(SUBJECTS_SYSTEM, CollectionCatalog.SYSTEM_COLLECTION);
    LOADED.put(SUBJECTS_USER, CollectionCatalog.USER_COLLECTION);
    LOADED.put(SUBJECTS_DEFAULT, CollectionCatalog.FACTORY_COLLECTION);
    LOADED.put(SUBJECTS_GROUP, CollectionCatalog.GROUP_COLLECTION);
    LOADED.put(SUBJECTS_ROLE_TEMPLATE, CollectionCatalog.GROUP_COLLECTION);
  }

  public SpongePermissionService() {
    instance = this;
  }

  // ======

  public static SpongePermissionService getInstance() {
    return instance;
  }

  // ======

  @Override
  @NotNull
  public SubjectCollection getUserSubjects() {
    return CollectionCatalog.USER_COLLECTION;
  }

  @Override
  @NotNull
  public SubjectCollection getGroupSubjects() {
    return CollectionCatalog.GROUP_COLLECTION;
  }

  @Override
  @NotNull
  public Subject getDefaults() {
    return SubjectDefaultData.DEFAULT;
  }

  @Override
  @NotNull
  public Predicate<String> getIdentifierValidityPredicate() {
    return s -> true;
  }

  @Override
  @NotNull
  public CompletableFuture<SubjectCollection> loadCollection(@NotNull String identifier) {
    return CompletableFuture.completedFuture(LOADED.getOrDefault(identifier, CollectionCatalog.FACTORY_COLLECTION));
  }

  @Override
  @NotNull
  public Optional<SubjectCollection> getCollection(@NotNull String identifier) {
    return Optional.ofNullable(this.loadCollection(identifier).join());
  }

  @Override
  @NotNull
  public CompletableFuture<Boolean> hasCollection(@NotNull String identifier) {
    return CompletableFuture.completedFuture(this.getCollection(identifier).isPresent());
  }

  @Override
  @NotNull
  public Map<String, SubjectCollection> getLoadedCollections() {
    return Collections.unmodifiableMap(LOADED);
  }

  @Override
  @NotNull
  public CompletableFuture<Set<String>> getAllIdentifiers() {
    return CompletableFuture.completedFuture(LOADED.keySet());
  }

  @Override
  @NotNull
  public SubjectReference newSubjectReference(
    @NotNull String collectionIdentifier,
    @NotNull String subjectIdentifier
  ) {
    return new SpongeSubjectReference(this, collectionIdentifier, subjectIdentifier);
  }

  @Override
  @NotNull
  public PermissionDescription.Builder newDescriptionBuilder(@NotNull Object plugin) {
    return new SpongePermissionDescriptionBuilder(this, Sponge.getPluginManager().fromInstance(plugin).orElse(null));
  }

  @Override
  @NotNull
  public Optional<PermissionDescription> getDescription(@NotNull String permission) {
    return Optional.ofNullable(DESCRIPTIONS.get(permission));
  }

  @Override
  @NotNull
  public Collection<PermissionDescription> getDescriptions() {
    return DESCRIPTIONS.values();
  }

  @Override
  public void registerContextCalculator(@Nullable ContextCalculator<Subject> calculator) {
  }
}
