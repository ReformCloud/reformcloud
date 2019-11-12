package systems.reformcloud.reformcloud2.executor.api.sponge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DependencyLoader;
import systems.reformcloud.reformcloud2.executor.api.common.language.loading.LanguageWorker;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;

@Plugin(
        id = "reformcloud_2_api_executor",
        name = "ReformCloud2SpongeExecutor",
        version = "2",
        description = "The reformcloud executor api",
        authors = {
                "derklaro",
                "ReformCloud-Team"
        },
        url = "https://reformcloud.systems"
)
public class SpongeLauncher {

    @Listener
    public void handle(final GameLoadCompleteEvent event) {
        DependencyLoader.doLoad();
        LanguageWorker.doLoad();
        StringUtil.sendHeader();
    }

    @Listener
    public void handle(final GameStartedServerEvent event) {
        Sponge.getChannelRegistrar().createChannel(this, "BungeeCord");
        new SpongeExecutor(this);
    }

    @Listener
    public void handle(final GameStoppingServerEvent event) {
        SpongeExecutor.getInstance().getNetworkClient().disconnect();
        Sponge.getServer().getOnlinePlayers().forEach(Player::kick);
    }
}
