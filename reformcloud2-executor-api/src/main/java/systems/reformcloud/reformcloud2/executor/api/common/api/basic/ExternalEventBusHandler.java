package systems.reformcloud.reformcloud2.executor.api.common.api.basic;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.in.event.*;
import systems.reformcloud.reformcloud2.executor.api.common.event.Event;
import systems.reformcloud.reformcloud2.executor.api.common.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

//Note! This class CANNOT use Reflections because it leads to problems using spigot (older guava implementation)
public final class ExternalEventBusHandler {

    /**
     * Initializes the external event bus which handles all known event packets and call them sync
     *
     * @param packetHandler The current packet handler per implementation
     * @param eventManager The event manager which should be used
     */
    public ExternalEventBusHandler(@Nonnull PacketHandler packetHandler, @Nonnull EventManager eventManager) {
        packetHandler.registerNetworkHandlers(
                new EventPacketInProcessClosed(),
                new EventPacketInProcessStarted(),
                new EventPacketInProcessUpdated(),
                new EventPacketInPlayerServerSwitch(),
                new EventPacketInLogoutPlayer(),
                new EventPacketInPlayerConnected()
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
    @Nonnull
    public EventManager getEventManager() {
        return eventManager;
    }

    /**
     * Get the current instance of the event bus handler
     *
     * @return the current instance of the event bus handler
     */
    @Nullable
    public static ExternalEventBusHandler getInstance() {
        return instance;
    }

    /**
     * Calls an event sync
     *
     * @param event The event which should be called
     */
    public void callEvent(@Nonnull Event event) {
        eventManager.callEvent(event);
    }
}
