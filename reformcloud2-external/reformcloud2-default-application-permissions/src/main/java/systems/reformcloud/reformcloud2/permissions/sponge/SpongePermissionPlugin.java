package systems.reformcloud.reformcloud2.permissions.sponge;

import com.google.inject.Inject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.permission.PermissionService;
import systems.reformcloud.reformcloud2.permissions.sponge.listener.SpongePermissionListener;
import systems.reformcloud.reformcloud2.permissions.sponge.service.SpongePermissionService;
import systems.reformcloud.reformcloud2.permissions.util.PermissionPluginUtil;

@Plugin(
        id = "reformcloud_2_perms",
        name = "SpongePermissionPlugin",
        version = "2",
        description = "The reformcloud permission plugin",
        authors = {"derklaro"},
        url = "https://reformcloud.systems",
        dependencies = {@Dependency(id = "reformcloud_2_api_executor")}
)
public class SpongePermissionPlugin {

    @Inject
    private ServiceManager serviceManager;

    @Listener
    public void handle(final GameStartedServerEvent event) {
        PermissionPluginUtil.awaitConnection();
        serviceManager.setProvider(this, PermissionService.class, new SpongePermissionService());

        Sponge.getEventManager().registerListeners(this, new SpongePermissionListener());
    }
}
