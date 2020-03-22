package systems.reformcloud.reformcloud2.proxy.plugin;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.proxy.ProxyConfiguration;

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

            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(
                    e -> ExecutorAPI.getInstance().getPacketHandler().getQueryHandler().sendQueryAsync(e, new PacketOutRequestConfig()).onComplete(result -> {
                        ProxyConfiguration configuration = result.content().get("result", ProxyConfiguration.TYPE);
                        if (configuration == null) {
                            return;
                        }

                        PluginConfigHandler.setConfiguration(configuration);
                        then.run();
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
