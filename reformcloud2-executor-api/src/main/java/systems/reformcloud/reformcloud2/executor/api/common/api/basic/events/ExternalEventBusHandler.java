/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.api.common.api.basic.events;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.shared.*;
import systems.reformcloud.reformcloud2.executor.api.common.event.Event;
import systems.reformcloud.reformcloud2.executor.api.common.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;

//Note! This class CANNOT use Reflections because it leads to problems using spigot (older guava implementation)
public class ExternalEventBusHandler {

    /**
     * Initializes the external event bus which handles all known event packets and call them sync
     *
     * @param packetHandler The current packet handler per implementation
     * @param eventManager  The event manager which should be used
     */
    public ExternalEventBusHandler(@NotNull PacketHandler packetHandler, @NotNull EventManager eventManager) {
        packetHandler.registerNetworkHandlers(
                EventPacketProcessClosed.class,
                EventPacketProcessStarted.class,
                EventPacketProcessUpdated.class,
                EventPacketPlayerServerSwitch.class,
                EventPacketLogoutPlayer.class,
                EventPacketPlayerConnected.class
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
