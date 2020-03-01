package systems.reformcloud.reformcloud2.executor.api.common.api.basic.events;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.event.Event;

public class ChannelMessageReceivedEvent extends Event {

    public ChannelMessageReceivedEvent(JsonConfiguration content, String baseChannel, String subChannel) {
        this.content = content;
        this.baseChannel = baseChannel;
        this.subChannel = subChannel;
    }

    private final JsonConfiguration content;

    private final String baseChannel;

    private final String subChannel;

    public JsonConfiguration getContent() {
        return content;
    }

    public String getBaseChannel() {
        return baseChannel;
    }

    public String getSubChannel() {
        return subChannel;
    }
}
