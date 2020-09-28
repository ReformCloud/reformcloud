/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.reformcloud2.shared.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.event.Event;
import systems.reformcloud.reformcloud2.executor.api.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.event.ListenerContainer;
import systems.reformcloud.reformcloud2.executor.api.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.task.Task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DefaultEventManager implements EventManager {

    private static final Comparator<ListenerContainer> PRIORITY_COMPARATOR = (c1, c2) -> (c1.getPriority().getPriority() > c2.getPriority().getPriority()) ? 1 : -1;
    private final List<ListenerContainer> registeredListeners = new CopyOnWriteArrayList<>();

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
        List<ListenerContainer> containers = new ArrayList<>();
        for (ListenerContainer registeredListener : this.registeredListeners) {
            if (registeredListener.getTargetEventClass().equals(event.getClass())) {
                containers.add(registeredListener);
            }
        }

        containers.sort(PRIORITY_COMPARATOR);
        for (ListenerContainer registeredListener : containers) {
            try {
                registeredListener.call(event);
            } catch (final InvocationTargetException | IllegalAccessException exception) {
                System.err.println("Exception posting event " + event.getClass().getName() + " to class " + registeredListener.getListenerInstance().getClass().getName());
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
            this.registeredListeners.add(container);
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
        this.registeredListeners.removeIf(listenerContainer -> listenerContainer.getListenerInstance() == listener);
    }

    @Override
    public void unregisterAll() {
        this.registeredListeners.clear();
    }

    @NotNull
    @Override
    public @UnmodifiableView List<ListenerContainer> getListeners() {
        return Collections.unmodifiableList(this.registeredListeners);
    }
}
