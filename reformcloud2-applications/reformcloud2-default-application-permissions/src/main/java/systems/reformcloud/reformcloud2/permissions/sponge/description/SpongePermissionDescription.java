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
package systems.reformcloud.reformcloud2.permissions.sponge.description;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SpongePermissionDescription implements PermissionDescription {

    private final PermissionService service;
    private final String id;
    private final PluginContainer owner;
    private final Text description;

    SpongePermissionDescription(
            @NotNull PermissionService service,
            @NotNull String id,
            @Nullable PluginContainer owner,
            @Nullable Text description
    ) {
        this.service = service;
        this.id = id;
        this.owner = owner;
        this.description = description;
    }

    @Override
    @NotNull
    public String getId() {
        return this.id;
    }

    @Override
    @NotNull
    public Optional<Text> getDescription() {
        return Optional.ofNullable(this.description);
    }

    @Override
    @NotNull
    public Optional<PluginContainer> getOwner() {
        return Optional.ofNullable(this.owner);
    }

    @Override
    @NotNull
    public CompletableFuture<Map<SubjectReference, Boolean>> findAssignedSubjects(@NotNull String collectionIdentifier) {
        return this.service.loadCollection(collectionIdentifier).thenCompose(e -> {
            if (e == null) {
                return CompletableFuture.completedFuture(new HashMap<>());
            }

            return e.getAllWithPermission(this.getId());
        });
    }

    @Override
    @NotNull
    public Map<Subject, Boolean> getAssignedSubjects(@NotNull String collectionIdentifier) {
        return this.service.getCollection(collectionIdentifier).map(e -> e.getLoadedWithPermission(this.getId())).orElseGet(HashMap::new);
    }
}
