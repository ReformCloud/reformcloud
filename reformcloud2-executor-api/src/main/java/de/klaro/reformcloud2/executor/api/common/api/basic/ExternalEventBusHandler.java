package de.klaro.reformcloud2.executor.api.common.api.basic;

import de.klaro.reformcloud2.executor.api.common.api.basic.packets.in.event.EventPacketInProcessClosed;
import de.klaro.reformcloud2.executor.api.common.api.basic.packets.in.event.EventPacketInProcessStarted;
import de.klaro.reformcloud2.executor.api.common.api.basic.packets.in.event.EventPacketInProcessUpdated;
import de.klaro.reformcloud2.executor.api.common.event.Event;
import de.klaro.reformcloud2.executor.api.common.event.EventManager;
import de.klaro.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;

//Note! This class CANNOT use Reflections because it leads to problems using spigot (older guava implementation)
public final class ExternalEventBusHandler {

    public ExternalEventBusHandler(PacketHandler packetHandler, EventManager eventManager) {
        packetHandler.registerNetworkHandlers(
                new EventPacketInProcessClosed(),
                new EventPacketInProcessStarted(),
                new EventPacketInProcessUpdated()
        );
        this.eventManager = eventManager;
        instance = this;
    }

    private static ExternalEventBusHandler instance;

    public void update(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public static ExternalEventBusHandler getInstance() {
        return instance;
    }

    private EventManager eventManager;

    public void callEvent(Event event) {
        eventManager.callEvent(event);
    }
}
