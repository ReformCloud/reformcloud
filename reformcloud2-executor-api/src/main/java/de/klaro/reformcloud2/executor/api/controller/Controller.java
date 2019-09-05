package de.klaro.reformcloud2.executor.api.controller;

import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.commands.manager.CommandManager;
import de.klaro.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import de.klaro.reformcloud2.executor.api.common.network.server.NetworkServer;
import de.klaro.reformcloud2.executor.api.common.utility.runtime.ReloadableRuntime;

public abstract class Controller extends ExecutorAPI implements ReloadableRuntime {

    protected abstract void bootstrap();

    public abstract void shutdown() throws Exception;

    public static Controller getInstance() {
        return (Controller) ExecutorAPI.getInstance();
    }

    public abstract NetworkServer getNetworkServer();

    public abstract PacketHandler getPacketHandler();

    public abstract CommandManager getCommandManager();
}
