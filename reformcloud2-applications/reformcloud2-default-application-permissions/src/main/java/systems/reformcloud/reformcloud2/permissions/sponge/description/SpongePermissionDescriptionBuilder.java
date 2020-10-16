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
package systems.reformcloud.reformcloud2.permissions.sponge.description;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.text.Text;
import systems.reformcloud.reformcloud2.permissions.sponge.service.SpongePermissionService;

public class SpongePermissionDescriptionBuilder implements PermissionDescription.Builder {

    private final PermissionService service;
    private final PluginContainer pluginContainer;
    private String id;

    private Text description;

    public SpongePermissionDescriptionBuilder(@NotNull PermissionService service, @Nullable PluginContainer plugin) {
        this.service = service;
        this.pluginContainer = plugin;
    }

    @Override
    @NotNull
    public PermissionDescription.Builder id(@NotNull String permissionId) {
        this.id = permissionId;
        return this;
    }

    @Override
    @NotNull
    public PermissionDescription.Builder description(@Nullable Text description) {
        this.description = description;
        return this;
    }

    @Override
    @NotNull
    public PermissionDescription.Builder assign(@Nullable String role, boolean value) {
        return this;
    }

    @Override
    @NotNull
    public PermissionDescription register() throws IllegalStateException {
        PermissionDescription permissionDescription = new SpongePermissionDescription(
            this.service, this.id, this.pluginContainer, this.description
        );
        SpongePermissionService.DESCRIPTIONS.put(permissionDescription.getId(), permissionDescription);
        return permissionDescription;
    }
}
