package systems.reformcloud.reformcloud2.signs.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import systems.reformcloud.reformcloud2.signs.bukkit.adapter.BukkitSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.ConfigRequesterUtil;

public class BukkitPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    ConfigRequesterUtil.requestSignConfigAsync(
        e -> new BukkitSignSystemAdapter(this, e));
  }

  @Override
  public void onDisable() {
    Bukkit.getScheduler().cancelTasks(this);
  }
}
