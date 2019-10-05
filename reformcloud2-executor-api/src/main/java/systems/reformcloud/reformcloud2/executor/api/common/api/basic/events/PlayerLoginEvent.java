package systems.reformcloud.reformcloud2.executor.api.common.api.basic.events;

import systems.reformcloud.reformcloud2.executor.api.common.event.Event;

public class PlayerLoginEvent extends Event {

    public PlayerLoginEvent(String name) {
        this.name = name;
    }

    private final String name;

    public String getName() {
        return name;
    }
}
