package systems.reformcloud.reformcloud2.permissions.sponge.description;

import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.text.Text;
import systems.reformcloud.reformcloud2.permissions.sponge.service.SpongePermissionService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpongePermissionDescriptionBuilder implements PermissionDescription.Builder {

    public SpongePermissionDescriptionBuilder(@Nonnull PermissionService service, @Nullable PluginContainer plugin) {
        this.service = service;
        this.pluginContainer = plugin;
    }

    private final PermissionService service;

    private String id;

    private Text description;

    private PluginContainer pluginContainer;

    @Override
    @Nonnull
    public PermissionDescription.Builder id(@Nonnull String permissionId) {
        this.id = permissionId;
        return this;
    }

    @Override
    @Nonnull
    public PermissionDescription.Builder description(@Nullable Text description) {
        this.description = description;
        return this;
    }

    @Override
    @Nonnull
    public PermissionDescription.Builder assign(@Nullable String role, boolean value) {
        return this;
    }

    @Override
    @Nonnull
    public PermissionDescription register() throws IllegalStateException {
        PermissionDescription permissionDescription = new SpongePermissionDescription(
                service, id, pluginContainer, description
        );
        SpongePermissionService.DESCRIPTIONS.put(permissionDescription.getId(), permissionDescription);
        return permissionDescription;
    }
}
