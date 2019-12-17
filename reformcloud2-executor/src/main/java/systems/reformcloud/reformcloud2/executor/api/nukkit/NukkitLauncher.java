package systems.reformcloud.reformcloud2.executor.api.nukkit;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import systems.reformcloud.reformcloud2.executor.api.common.language.loading.LanguageWorker;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.nukkit.event.ExtraListenerHandler;
import systems.reformcloud.reformcloud2.executor.api.nukkit.event.PlayerListenerHandler;

public final class NukkitLauncher extends PluginBase {

  @Override
  public void onLoad() {
    LanguageWorker.doLoad();
    StringUtil.sendHeader();
  }

  @Override
  public void onEnable() {
    new NukkitExecutor(this);

    Server.getInstance().getPluginManager().registerEvents(
        new PlayerListenerHandler(), this);
    Server.getInstance().getPluginManager().registerEvents(
        new ExtraListenerHandler(), this);
  }

  @Override
  public void onDisable() {
    Server.getInstance().getScheduler().cancelTask(this);
    NukkitExecutor.getInstance().getNetworkClient().disconnect();

    Server.getInstance().getOnlinePlayers().values().forEach(Player::kick);
  }
}
