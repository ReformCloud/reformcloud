package systems.reformcloud.reformcloud2.signs.sponge;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import systems.reformcloud.reformcloud2.signs.sponge.adapter.SpongeSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.ConfigRequesterUtil;

@Plugin(
        id = "reformcloud_2_signs",
        name = "SpongeSignsPlugin",
        version = "2",
        description = "The reformcloud signs plugin",
        authors = {"derklaro"},
        url = "https://reformcloud.systems",
        dependencies = {@Dependency(id = "reformcloud_2_api_executor")}
)
public class SpongePlugin {

    @Inject
    private Logger logger;

    @Listener
    public void handle(final GameStartedServerEvent event) {
        ConfigRequesterUtil.requestSignConfigAsync(e -> new SpongeSignSystemAdapter(this, e));
    }
}
