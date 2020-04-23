package systems.reformcloud.reformcloud2.executor.api.common.api.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.shared.*;
import systems.reformcloud.reformcloud2.executor.api.common.event.Event;
import systems.reformcloud.reformcloud2.executor.api.common.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;

//Note! This class CANNOT use Reflections because it leads to problems using spigot (older guava implementation)
public final class ExternalEventBusHandler {

    /**
     * Initializes the external event bus which handles all known event packets and call them sync
     *
     * @param packetHandler The current packet handler per implementation
     * @param eventManager The event manager which should be used
     */
    public ExternalEventBusHandler(@NotNull PacketHandler packetHandler, @NotNull EventManager eventManager) {
        packetHandler.registerNetworkHandlers(
                new EventPacketProcessClosed(),
                new EventPacketProcessStarted(),
                new EventPacketProcessUpdated(),
                new EventPacketPlayerServerSwitch(),
                new EventPacketLogoutPlayer(),
                new EventPacketPlayerConnected()
        );

        this.eventManager = eventManager;
        instance = this;
    }

    private final EventManager eventManager;

    private static ExternalEventBusHandler instance;

    /**
     * Gets the current event manager which is used for the external event bus
     *
     * @return the current event manager
     */
    @NotNull
    public EventManager getEventManager() {
        return eventManager;
    }

    /**
     * Get the current instance of the event bus handler
     *
     * @return the current instance of the event bus handler
     */
    @NotNull
    public static ExternalEventBusHandler getInstance() {
        return instance;
    }

    /**
     * Calls an event sync
     *
     * @param event The event which should be called
     */
    public void callEvent(@NotNull Event event) {
        eventManager.callEvent(event);
    }
}
