package systems.reformcloud.reformcloud2.executor.api.common.event.basic;

import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.event.Event;
import systems.reformcloud.reformcloud2.executor.api.common.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.common.event.LoadedListener;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class DefaultEventManager implements EventManager {

    private final Lock lock = new ReentrantLock();

    private final Map<Class<?>, Map<Byte, Map<Object, Method[]>>> byListenerAndPriority = new HashMap<>();

    private final Map<Class<?>, List<LoadedListener>> done = new ConcurrentHashMap<>();

    @Override
    public void callEvent(Class<? extends Event> event) {
        try {
            callEvent(event.newInstance());
        } catch (final IllegalAccessException | InstantiationException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void callEvent(Event event) {
        event.preCall();

        List<LoadedListener> listeners = done.get(event.getClass());
        if (listeners != null) {
            listeners.forEach(loadedListener -> {
                if (!event.isAsync()) {
                    try {
                        loadedListener.call(event);
                    } catch (final InvocationTargetException | IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    CompletableFuture.runAsync(() -> {
                        try {
                            loadedListener.call(event);
                        } catch (final InvocationTargetException | IllegalAccessException ex) {
                            ex.printStackTrace();
                        }
                    });
                }
            });
        }

        event.postCall();
    }

    @Override
    public void callEventAsync(Class<? extends Event> event) {
        CompletableFuture.runAsync(() -> callEvent(event));
    }

    @Override
    public void callEventAsync(Event event) {
        CompletableFuture.runAsync(() -> callEvent(event));
    }

    @Override
    public void registerListener(Object listener) {
        this.register(listener);
    }

    @Override
    public void registerListener(Class<?> listener) {
        try {
            this.register(listener.newInstance());
        } catch (final InstantiationException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void registerListenerAsync(Object listener) {
        CompletableFuture.runAsync(() -> registerListener(listener));
    }

    @Override
    public void registerListenerAsync(Class<?> listener) {
        CompletableFuture.runAsync(() -> registerListener(listener));
    }

    @Override
    public void unregisterListener(Object listener) {
        unregister(listener);
    }

    @Override
    public void unregisterAll() {
        Links.forEachValues(done, loadedListeners -> Links.forEach(loadedListeners, loadedListener -> unregister(loadedListener.getListener())));
    }

    @Nonnull
    @Override
    public List<List<LoadedListener>> getListeners() {
        return Collections.unmodifiableList(Links.getValues(done, aClass -> true));
    }

    private Map<Class<?>, Map<Byte, Set<Method>>> find(Object listener) {
        Map<Class<?>, Map<Byte, Set<Method>>> result = new HashMap<>();
        for (Method method : listener.getClass().getDeclaredMethods()) {
            Listener annotation = method.getAnnotation(Listener.class);
            if (annotation != null) {
                Class<?>[] parameters = method.getParameterTypes();
                Conditions.isTrue(parameters.length == 1, "Listener class {0} tried to register a method with {1} instead of one argument",
                        listener.getClass().getSimpleName(), parameters.length);

                Map<Byte, Set<Method>> map = result.computeIfAbsent(parameters[0], aClass -> new HashMap<>());

                Set<Method> methods = map.computeIfAbsent(annotation.priority().getPriority(), aByte -> new HashSet<>());

                methods.add(method);
            }
        }

        return result;
    }

    private void register(Object listener) {
        Map<Class<?>, Map<Byte, Set<Method>>> handlers = find(listener);
        lock.lock();
        try {
            for (Map.Entry<Class<?>, Map<Byte, Set<Method>>> classMapEntry : handlers.entrySet()) {
                Map<Byte, Map<Object, Method[]>> priorities = byListenerAndPriority.computeIfAbsent(classMapEntry.getKey(), aClass -> new HashMap<>());

                for (Map.Entry<Byte, Set<Method>> byteSetEntry : classMapEntry.getValue().entrySet()) {
                    Map<Object, Method[]> current = priorities.computeIfAbsent(byteSetEntry.getKey(), aByte -> new HashMap<>());

                    Method[] methods = new Method[byteSetEntry.getValue().size()];
                    current.put(listener, byteSetEntry.getValue().toArray(methods));
                }

                finallyDo(classMapEntry.getKey());
            }
        } finally {
            lock.unlock();
        }
    }

    private void finallyDo(Class<?> eventClass) {
        Map<Byte, Map<Object, Method[]>> map = byListenerAndPriority.get(eventClass);
        if (map != null) {
            List<LoadedListener> listeners = new ArrayList<>();

            byte current = Byte.MIN_VALUE;
            do {
                Map<Object, Method[]> handlers = map.get(current);
                if (handlers != null) {
                    for (Map.Entry<Object, Method[]> objectEntry : handlers.entrySet()) {
                        for (Method method : objectEntry.getValue()) {
                            LoadedListener loadedListener = new LoadedListener(objectEntry.getKey(), method);
                            listeners.add(loadedListener);
                        }
                    }
                }
            } while (current++ < Byte.MAX_VALUE);
            done.put(eventClass, listeners);
        } else {
            done.remove(eventClass);
        }
    }

    private void unregister(Object listener) {
        Map<Class<?>, Map<Byte, Set<Method>>> handler = find(listener);
        lock.lock();
        try {
            for (Map.Entry<Class<?>, Map<Byte, Set<Method>>> classMapEntry : handler.entrySet()) {
                Map<Byte, Map<Object, Method[]>> prioritiesMap = byListenerAndPriority.get(classMapEntry.getKey());
                if (prioritiesMap != null) {
                    for (Byte aByte : classMapEntry.getValue().keySet()) {
                        Map<Object, Method[]> currentPriority = prioritiesMap.get(aByte);
                        if (currentPriority != null) {
                            currentPriority.remove(listener);
                            if (currentPriority.isEmpty()) {
                                prioritiesMap.remove(aByte);
                            }
                        }
                    }

                    if (prioritiesMap.isEmpty()) {
                        byListenerAndPriority.remove(classMapEntry.getKey());
                    }
                }

                finallyDo(classMapEntry.getKey());
            }
        } finally {
            lock.unlock();
        }
    }
}
