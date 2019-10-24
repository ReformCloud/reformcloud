package systems.reformcloud.reformcloud2.executor.api.common.api.basic.events;

import systems.reformcloud.reformcloud2.executor.api.common.event.Event;

/**
 * Gets called when a player logged in on the network
 */
public class PlayerLoginEvent extends Event {

    /**
     * Creates a new player login event
     *
     * @param name The name of the player who logs in
     */
    public PlayerLoginEvent(String name) {
        this.name = name;
    }

    private final String name;

    /**
     * @return The name of the player
     */
    public String getName() {
        return name;
    }
}
