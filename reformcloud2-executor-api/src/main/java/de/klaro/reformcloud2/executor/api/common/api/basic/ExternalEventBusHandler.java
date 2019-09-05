package de.klaro.reformcloud2.executor.api.common.api.basic;

import de.klaro.reformcloud2.executor.api.common.event.Event;
import de.klaro.reformcloud2.executor.api.common.event.EventManager;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import de.klaro.reformcloud2.executor.api.common.utility.reflections.ReflectionsImpl;

import java.util.function.Consumer;

public final class ExternalEventBusHandler {

    public ExternalEventBusHandler(PacketHandler packetHandler, EventManager eventManager) {
        new ReflectionsImpl("de.klaro.reformcloud2.executor.api.common.api.basic.packet.in.event").getSubTypesOf(NetworkHandler.class).forEach(new Consumer<Class<? extends NetworkHandler>>() {
            @Override
            public void accept(Class<? extends NetworkHandler> aClass) {
                packetHandler.registerHandler(aClass);
            }
        });
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
