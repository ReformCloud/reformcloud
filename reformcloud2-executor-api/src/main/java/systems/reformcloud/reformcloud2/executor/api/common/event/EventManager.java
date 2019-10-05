package systems.reformcloud.reformcloud2.executor.api.common.event;

import java.util.List;

public interface EventManager {

    void callEvent(Class<? extends Event> event);

    void callEvent(Event event);

    void callEventAsync(Class<? extends Event> event);

    void callEventAsync(Event event);

    void registerListener(Object listener);

    void registerListener(Class<?> listener);

    void registerListenerAsync(Object listener);

    void registerListenerAsync(Class<?> listener);

    void unregisterListener(Object listener);

    void unregisterAll();

    List<List<LoadedListener>> getListeners();
}
