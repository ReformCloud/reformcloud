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
package systems.reformcloud.reformcloud2.executor.api.common.event.basic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.common.event.Event;
import systems.reformcloud.reformcloud2.executor.api.common.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.common.event.ListenerContainer;
import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DefaultEventManager implements EventManager {

    private final Map<Class<?>, List<ListenerContainer>> registeredListeners = new ConcurrentHashMap<>();

    @Nullable
    @Override
    public <T extends Event> T callEvent(@NotNull Class<? extends T> event) {
        try {
            return this.callEvent(event.getDeclaredConstructor().newInstance());
        } catch (final NoSuchMethodException exception) {
            System.err.println("Missing NoArgsConstructor in event class " + event.getName());
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    @NotNull
    @Override
    public <T extends Event> T callEvent(@NotNull T event) {
        Collection<ListenerContainer> registeredListeners = this.registeredListeners.get(event.getClass());
        if (registeredListeners == null) {
            return event;
        }

        for (ListenerContainer registeredListener : registeredListeners) {
            try {
                registeredListener.call(event);
            } catch (final InvocationTargetException | IllegalAccessException exception) {
                System.err.println("Unable to call event " + event.getClass().getName());
                exception.printStackTrace();
            }
        }

        return event;
    }

    @NotNull
    @Override
    public <T extends Event> Task<T> callEventAsync(@NotNull Class<? extends T> event) {
        return Task.supply(() -> this.callEvent(event));
    }

    @NotNull
    @Override
    public <T extends Event> Task<T> callEventAsync(@NotNull T event) {
        return Task.supply(() -> this.callEvent(event));
    }

    @Override
    public void registerListener(@NotNull Object listener) {
        for (Method declaredMethod : listener.getClass().getDeclaredMethods()) {
            Listener annotation = declaredMethod.getAnnotation(Listener.class);
            if (annotation == null) {
                continue;
            }

            Class<?>[] parameters = declaredMethod.getParameterTypes();
            if (parameters.length != 1) {
                System.err.println("Unable to register listener method " + declaredMethod.getName()
                        + "@" + listener.getClass().getName() + " because method has more or less than one parameter");
                continue;
            }

            if (!Event.class.isAssignableFrom(parameters[0])) {
                System.err.println("Unable to register listener method " + declaredMethod.getName()
                        + "@" + listener.getClass().getName() + " because parameter type " + parameters[0].getName()
                        + " is not assignable from " + Event.class.getName());
                continue;
            }

            ListenerContainer container = new DefaultListenerContainer(parameters[0], listener, declaredMethod, annotation.priority());

            this.registeredListeners.putIfAbsent(parameters[0], new CopyOnWriteArrayList<>());
            this.registeredListeners.get(parameters[0]).add(container);

            this.registeredListeners.get(parameters[0]).sort(Comparator.comparingInt(t0 -> t0.getPriority().getPriority()));
        }
    }

    @Override
    public void registerListener(@NotNull Class<?> listener) {
        try {
            this.registerListener(listener.getDeclaredConstructor().newInstance());
        } catch (final NoSuchMethodException exception) {
            System.err.println("Missing NoArgsConstructor in listener class " + listener.getName());
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void unregisterListener(@NotNull Object listener) {
        for (List<ListenerContainer> value : this.registeredListeners.values()) {
            value.removeIf(listenerContainer -> listenerContainer.getListenerInstance() == listener);
        }
    }

    @Override
    public void unregisterAll() {
        this.registeredListeners.clear();
    }

    @NotNull
    @Override
    public @UnmodifiableView List<ListenerContainer> getListeners() {
        List<ListenerContainer> containers = new ArrayList<>();
        for (List<ListenerContainer> value : this.registeredListeners.values()) {
            containers.addAll(value);
        }

        return containers;
    }
}
