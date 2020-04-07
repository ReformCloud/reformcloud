package systems.reformcloud.reformcloud2.permissions.sponge.description;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.text.Text;
import systems.reformcloud.reformcloud2.permissions.sponge.service.SpongePermissionService;

public class SpongePermissionDescriptionBuilder implements PermissionDescription.Builder {

    public SpongePermissionDescriptionBuilder(@NotNull PermissionService service, @Nullable PluginContainer plugin) {
        this.service = service;
        this.pluginContainer = plugin;
    }

    private final PermissionService service;

    private String id;

    private Text description;

    private final PluginContainer pluginContainer;

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
                service, id, pluginContainer, description
        );
        SpongePermissionService.DESCRIPTIONS.put(permissionDescription.getId(), permissionDescription);
        return permissionDescription;
    }
}
