package systems.reformcloud.reformcloud2.proxy.application;

import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.common.application.updater.ApplicationUpdateRepository;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.proxy.application.command.CommandProxy;
import systems.reformcloud.reformcloud2.proxy.application.listener.ProcessInclusionHandler;
import systems.reformcloud.reformcloud2.proxy.application.network.PacketQueryInRequestConfig;
import systems.reformcloud.reformcloud2.proxy.application.updater.ProxyAddonUpdater;

import javax.annotation.Nullable;

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
        this.getCommandManager().register(new CommandProxy());

        ExecutorAPI.getInstance().getPacketHandler().registerHandler(new PacketQueryInRequestConfig());
    }

    @Override
    public void onDisable() {
        ExecutorAPI.getInstance().getPacketHandler().unregisterNetworkHandlers(NetworkUtil.EXTERNAL_BUS + 2);
    }

    public static ProxyApplication getInstance() {
        return instance;
    }

    public void reloadConfig() {
        ConfigHelper.init(dataFolder());
    }

    @Nullable
    @Override
    public ApplicationUpdateRepository getUpdateRepository() {
        return REPOSITORY;
    }

    private CommandManager getCommandManager() {
        return ExecutorAPI.getInstance().getType().equals(ExecutorType.CONTROLLER)
                ? ControllerExecutor.getInstance().getCommandManager()
                : NodeExecutor.getInstance().getCommandManager();
    }
}
