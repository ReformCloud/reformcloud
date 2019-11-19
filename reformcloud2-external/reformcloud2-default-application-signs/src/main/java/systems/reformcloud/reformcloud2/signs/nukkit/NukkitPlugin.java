package systems.reformcloud.reformcloud2.signs.nukkit;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import systems.reformcloud.reformcloud2.signs.nukkit.adapter.NukkitSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.util.ConfigRequesterUtil;

public class NukkitPlugin extends PluginBase {

    @Override
    public void onEnable() {
        ConfigRequesterUtil.requestSignConfigAsync(e -> new NukkitSignSystemAdapter(this, e));
    }

    @Override
    public void onDisable() {
        Server.getInstance().getScheduler().cancelTask(this);
    }
}
