package systems.reformcloud.reformcloud2.signs.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.signs.bukkit.adapter.BukkitSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.packets.api.out.APIPacketOutRequestSignLayouts;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;

public class BukkitPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Task.EXECUTOR.execute(() -> {
            while (!DefaultChannelManager.INSTANCE.get("Controller").isPresent()) {
                AbsoluteThread.sleep(200);
            }

            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(e -> ExecutorAPI.getInstance().getPacketHandler().getQueryHandler().sendQueryAsync(e, new APIPacketOutRequestSignLayouts()
            ).onComplete(r -> new BukkitSignSystemAdapter(this, r.content().get("config", SignConfig.TYPE))));
        });
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }
}
