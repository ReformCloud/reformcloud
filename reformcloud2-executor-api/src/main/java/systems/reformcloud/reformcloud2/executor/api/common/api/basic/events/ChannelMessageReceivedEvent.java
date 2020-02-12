package systems.reformcloud.reformcloud2.executor.api.common.api.basic.events;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.event.Event;

public class ChannelMessageReceivedEvent extends Event {

    public ChannelMessageReceivedEvent(JsonConfiguration content) {
        this.content = content;
    }

    private final JsonConfiguration content;

    public JsonConfiguration getContent() {
        return content;
    }
}
