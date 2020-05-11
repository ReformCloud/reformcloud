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
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractSpongeSubject implements Subject {

    //  =====

    protected abstract PermissionService service();

    protected abstract boolean has(String permission);

    // ======

    @Override
    @NotNull
    public Optional<CommandSource> getCommandSource() {
        return Optional.empty();
    }

    @Override
    @NotNull
    public final Optional<String> getFriendlyIdentifier() {
        return Optional.empty();
    }

    @Override
    @NotNull
    public SubjectData getTransientSubjectData() {
        return getSubjectData();
    }

    @Override
    public boolean isSubjectDataPersisted() {
        return false;
    }

    @Override
    @NotNull
    public SubjectReference asSubjectReference() {
        return service().newSubjectReference(getContainingCollection().getIdentifier(), getIdentifier());
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return has(permission);
    }

    @Override
    public boolean hasPermission(@Nullable Set<Context> contexts, @NotNull String permission) {
        return hasPermission(permission);
    }

    @Override
    @NotNull
    public Tristate getPermissionValue(@Nullable Set<Context> contexts, @NotNull String permission) {
        return hasPermission(permission) ? Tristate.TRUE : Tristate.FALSE;
    }

    @Override
    public boolean isChildOf(@Nullable SubjectReference parent) {
        return false;
    }

    @Override
    public boolean isChildOf(@Nullable Set<Context> contexts, @Nullable SubjectReference parent) {
        return false;
    }

    @Override
    @NotNull
    public List<SubjectReference> getParents() {
        return new ArrayList<>();
    }

    @Override
    @NotNull
    public List<SubjectReference> getParents(@Nullable Set<Context> contexts) {
        return new ArrayList<>();
    }

    @Override
    @NotNull
    public Optional<String> getOption(@Nullable String key) {
        return Optional.empty();
    }

    @Override
    @NotNull
    public Optional<String> getOption(@Nullable Set<Context> contexts, @Nullable String key) {
        return Optional.empty();
    }

    @Override
    @NotNull
    public Set<Context> getActiveContexts() {
        return SubjectData.GLOBAL_CONTEXT;
    }
}
