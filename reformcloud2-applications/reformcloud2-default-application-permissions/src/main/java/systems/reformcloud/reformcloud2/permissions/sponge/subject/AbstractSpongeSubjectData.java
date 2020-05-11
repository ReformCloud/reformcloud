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
package systems.reformcloud.reformcloud2.permissions.sponge.subject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSpongeSubjectData implements SubjectData {

    @Override
    @NotNull
    public CompletableFuture<Boolean> setPermission(@Nullable Set<Context> contexts, @Nullable String permission, @Nullable Tristate value) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @NotNull
    public CompletableFuture<Boolean> clearPermissions() {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @NotNull
    public CompletableFuture<Boolean> clearPermissions(@Nullable Set<Context> contexts) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @NotNull
    public CompletableFuture<Boolean> addParent(@Nullable Set<Context> contexts, @Nullable SubjectReference parent) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @NotNull
    public CompletableFuture<Boolean> removeParent(@Nullable Set<Context> contexts, @Nullable SubjectReference parent) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @NotNull
    public CompletableFuture<Boolean> clearParents() {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @NotNull
    public CompletableFuture<Boolean> clearParents(@Nullable Set<Context> contexts) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @NotNull
    public Map<Set<Context>, Map<String, String>> getAllOptions() {
        return new ConcurrentHashMap<>();
    }

    @Override
    @NotNull
    public Map<String, String> getOptions(@Nullable Set<Context> contexts) {
        return new ConcurrentHashMap<>();
    }

    @Override
    @NotNull
    public CompletableFuture<Boolean> setOption(@Nullable Set<Context> contexts, @Nullable String key, @Nullable String value) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @NotNull
    public CompletableFuture<Boolean> clearOptions() {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @NotNull
    public CompletableFuture<Boolean> clearOptions(@Nullable Set<Context> contexts) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @NotNull
    public List<SubjectReference> getParents(@Nullable Set<Context> contexts) {
        return new ArrayList<>();
    }

    @Override
    @NotNull
    public Map<Set<Context>, List<SubjectReference>> getAllParents() {
        return new ConcurrentHashMap<>();
    }
}
