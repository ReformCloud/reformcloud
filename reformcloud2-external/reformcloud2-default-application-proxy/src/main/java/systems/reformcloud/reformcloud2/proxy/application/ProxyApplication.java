package systems.reformcloud.reformcloud2.proxy.application;

import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.common.application.updater.ApplicationUpdateRepository;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.proxy.application.listener.ProcessInclusionHandler;
import systems.reformcloud.reformcloud2.proxy.application.network.PacketRequestConfig;
import systems.reformcloud.reformcloud2.proxy.application.updater.ProxyAddonUpdater;
import systems.reformcloud.reformcloud2.proxy.network.PacketProxyConfigUpdate;

public class ProxyApplication extends Application {

    private static final ApplicationUpdateRepository REPOSITORY = new ProxyAddonUpdater();

    private static ProxyApplication instance;

    @Override
    public void onInstallable() {
        ExecutorAPI.getInstance().getEventManager().registerListener(new ProcessInclusionHandler());
    }

    @Override
    public void onLoad() {
        instance = this;
        ConfigHelper.init(dataFolder());

        ExecutorAPI.getInstance().getPacketHandler().registerHandler(PacketRequestConfig.class);
        DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new PacketProxyConfigUpdate(ConfigHelper.getProxyConfiguration())));
    }

    public static ProxyApplication getInstance() {
        return instance;
    }

    @Nullable
    @Override
    public ApplicationUpdateRepository getUpdateRepository() {
        return REPOSITORY;
    }
}
