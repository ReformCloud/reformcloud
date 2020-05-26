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
package systems.reformcloud.reformcloud2.executor.api.event.defaults;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.event.Event;
import systems.reformcloud.reformcloud2.executor.api.event.ListenerContainer;
import systems.reformcloud.reformcloud2.executor.api.event.priority.EventPriority;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class DefaultListenerContainer implements ListenerContainer {

    private final Class<?> eventClassTarget;
    private final Object listenerInstance;
    private final Method method;
    private final EventPriority priority;

    DefaultListenerContainer(Class<?> eventClassTarget, Object listenerInstance, Method method, EventPriority priority) {
        this.eventClassTarget = eventClassTarget;
        this.listenerInstance = listenerInstance;
        this.method = method;
        this.priority = priority;
    }

    @NotNull
    @Override
    public Object getListenerInstance() {
        return this.listenerInstance;
    }

    @NotNull
    @Override
    public Class<?> getTargetEventClass() {
        return this.eventClassTarget;
    }

    @NotNull
    @Override
    public EventPriority getPriority() {
        return this.priority;
    }

    @Override
    public void call(@NotNull Event event) throws InvocationTargetException, IllegalAccessException {
        this.method.invoke(this.listenerInstance, event);
    }
}
