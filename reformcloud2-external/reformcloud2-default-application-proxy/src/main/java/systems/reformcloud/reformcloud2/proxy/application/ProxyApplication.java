package systems.reformcloud.reformcloud2.proxy.application;

import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.common.application.updater.ApplicationUpdateRepository;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.proxy.application.listener.ProcessInclusionHandler;
import systems.reformcloud.reformcloud2.proxy.application.network.PacketOutConfigUpdate;
import systems.reformcloud.reformcloud2.proxy.application.network.PacketQueryInRequestConfig;
import systems.reformcloud.reformcloud2.proxy.application.updater.ProxyAddonUpdater;

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

        ExecutorAPI.getInstance().getPacketHandler().registerHandler(new PacketQueryInRequestConfig());
        DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new PacketOutConfigUpdate()));
    }

    @Override
    public void onDisable() {
        ExecutorAPI.getInstance().getPacketHandler().unregisterNetworkHandlers(NetworkUtil.EXTERNAL_BUS + 2);
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
