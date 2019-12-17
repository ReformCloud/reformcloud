package systems.reformcloud.reformcloud2.executor.api.common.event;

/**
 * Represents a callable event of the runtime
 *
 * @see EventManager#callEvent(Event)
 * @see EventManager#registerListener(Object)
 */
public class Event {

    public Event() {
        this(false);
    }

    public Event(boolean isAsync) {
        this.async = isAsync;
    }

    private final boolean async;

    public final boolean isAsync() {
        return async;
    }

    public void preCall() {
    }

    public void postCall() {
    }
}
