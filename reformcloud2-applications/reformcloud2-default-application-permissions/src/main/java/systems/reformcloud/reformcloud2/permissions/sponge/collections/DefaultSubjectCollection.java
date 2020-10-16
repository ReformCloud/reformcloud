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
package systems.reformcloud.reformcloud2.permissions.sponge.collections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class DefaultSubjectCollection implements SubjectCollection {

    protected final PermissionService service;
    private final String type;

    public DefaultSubjectCollection(String type, PermissionService service) {
        this.type = type;
        this.service = service;
    }

    // ====

    @NotNull
    protected abstract Subject load(String id);

    // ===

    @Override
    @NotNull
    public String getIdentifier() {
        return this.type;
    }

    @Override
    @NotNull
    public Predicate<String> getIdentifierValidityPredicate() {
        return s -> true;
    }

    @Override
    @NotNull
    public SubjectReference newSubjectReference(@NotNull String subjectIdentifier) {
        return this.service.newSubjectReference(this.type, subjectIdentifier);
    }

    @Override
    @NotNull
    public CompletableFuture<Subject> loadSubject(@NotNull String identifier) {
        return CompletableFuture.completedFuture(this.load(identifier));
    }

    @Override
    @NotNull
    public Optional<Subject> getSubject(@NotNull String identifier) {
        return Optional.of(this.load(identifier));
    }

    @Override
    @NotNull
    public CompletableFuture<Map<String, Subject>> loadSubjects(@NotNull Set<String> identifiers) {
        Map<String, Subject> ref = new ConcurrentHashMap<>();
        identifiers.forEach(id -> ref.put(id, this.load(id)));
        return CompletableFuture.completedFuture(ref);
    }

    @Override
    @NotNull
    public Map<Subject, Boolean> getLoadedWithPermission(@NotNull String permission) {
        return this.getLoadedWithPermission(null, permission);
    }

    @Override
    @NotNull
    public Map<Subject, Boolean> getLoadedWithPermission(
        @Nullable Set<Context> contexts,
        @NotNull String permission
    ) {
        Map<Subject, Boolean> out = new ConcurrentHashMap<>();
        this.getLoadedSubjects().forEach(e -> {
            Tristate tristate = e.getPermissionValue(contexts == null ? e.getActiveContexts() : contexts, permission);
            if (tristate.equals(Tristate.UNDEFINED)) {
                return;
            }

            out.put(e, tristate.asBoolean());
        });
        return out;
    }

    @Override
    @NotNull
    public CompletableFuture<Map<SubjectReference, Boolean>> getAllWithPermission(@NotNull String permission) {
        return CompletableFuture.completedFuture(this.getLoadedWithPermission(permission).entrySet().stream().collect(Collectors.toMap(
            e -> e.getKey().asSubjectReference(),
            Map.Entry::getValue
        )));
    }

    @Override
    @NotNull
    public CompletableFuture<Map<SubjectReference, Boolean>> getAllWithPermission(@NotNull Set<Context> contexts, @NotNull String permission) {
        return CompletableFuture.completedFuture(this.getLoadedWithPermission(contexts, permission).entrySet().stream().collect(Collectors.toMap(
            e -> e.getKey().asSubjectReference(),
            Map.Entry::getValue
        )));
    }

    @Override
    @NotNull
    public CompletableFuture<Set<String>> getAllIdentifiers() {
        return CompletableFuture.completedFuture(this.getLoadedSubjects().stream()
            .map(Subject::getIdentifier)
            .collect(Collectors.toSet())
        );
    }

    @Override
    @NotNull
    public Subject getDefaults() {
        return this.service.getDefaults();
    }

    @Override
    public final void suggestUnload(@NotNull String identifier) {
    }
}
