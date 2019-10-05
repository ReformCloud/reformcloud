package systems.reformcloud.reformcloud2.executor.api.common.event;

public class Event {

    public Event() {
        this(false);
    }

    public Event(boolean isAsync) {
        this.async = isAsync;
    }

    private final boolean async;

    public boolean isAsync() {
        return async;
    }

    public void preCall() {
    }

    public void postCall() {
    }
}
