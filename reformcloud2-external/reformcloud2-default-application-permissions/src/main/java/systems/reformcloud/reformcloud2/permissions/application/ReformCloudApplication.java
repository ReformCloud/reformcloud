package systems.reformcloud.reformcloud2.permissions.application;

import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.common.application.updater.ApplicationUpdateRepository;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.application.command.CommandPerms;
import systems.reformcloud.reformcloud2.permissions.application.listener.ProcessInclusionHandler;
import systems.reformcloud.reformcloud2.permissions.application.updater.PermissionsAddonUpdater;
import systems.reformcloud.reformcloud2.permissions.packets.PacketHelper;

public class ReformCloudApplication extends Application {

    private static final ApplicationUpdateRepository REPOSITORY = new PermissionsAddonUpdater();

    private static ReformCloudApplication instance;

    @Override
    public void onInstallable() {
        ExecutorAPI.getInstance().getEventManager().registerListener(new ProcessInclusionHandler());
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        PacketHelper.addPacketHandler();
        PermissionAPI.handshake();

        getCommandManager().register(new CommandPerms());
    }

    @Nullable
    @Override
    public ApplicationUpdateRepository getUpdateRepository() {
        return REPOSITORY;
    }

    public static ReformCloudApplication getInstance() {
        return instance;
    }

    private CommandManager getCommandManager() {
        return ExecutorAPI.getInstance().getType().equals(ExecutorType.CONTROLLER)
                ? ControllerExecutor.getInstance().getCommandManager()
                : NodeExecutor.getInstance().getCommandManager();
    }
}
