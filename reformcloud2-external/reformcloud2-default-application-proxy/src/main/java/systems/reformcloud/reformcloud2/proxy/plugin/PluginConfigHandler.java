package systems.reformcloud.reformcloud2.proxy.plugin;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.proxy.ProxyConfiguration;
import systems.reformcloud.reformcloud2.proxy.application.network.PacketRequestConfig;
import systems.reformcloud.reformcloud2.proxy.application.network.PacketRequestConfigResult;

public final class PluginConfigHandler {

    private PluginConfigHandler() {
        throw new UnsupportedOperationException();
    }

    private static ProxyConfiguration configuration;

    public static void request(Runnable then) {
        Task.EXECUTOR.execute(() -> {
            while (!DefaultChannelManager.INSTANCE.get("Controller").isPresent()) {
                AbsoluteThread.sleep(20);
            }

            ExecutorAPI.getInstance().getPacketHandler().registerHandler(PacketRequestConfigResult.class);
            DefaultChannelManager.INSTANCE
                    .get("Controller")
                    .ifPresent(e -> ExecutorAPI.getInstance().getPacketHandler().getQueryHandler().sendQueryAsync(e, new PacketRequestConfig()).onComplete(result -> {
                                if (result instanceof PacketRequestConfigResult) {
                                    PluginConfigHandler.setConfiguration(((PacketRequestConfigResult) result).getProxyConfiguration());
                                    then.run();
                                }
                            })
                    );
        });
    }

    public static void setConfiguration(ProxyConfiguration configuration) {
        PluginConfigHandler.configuration = configuration;
    }

    public static ProxyConfiguration getConfiguration() {
        return configuration;
    }
}
