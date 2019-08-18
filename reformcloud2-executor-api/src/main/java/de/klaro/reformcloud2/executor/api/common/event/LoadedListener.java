package de.klaro.reformcloud2.executor.api.common.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class LoadedListener {

    public LoadedListener(Object listener, Method method) {
        this.listener = listener;
        this.method = method;
    }

    private final Object listener;

    private final Method method;

    public void call(Event event) throws InvocationTargetException, IllegalAccessException {
        method.invoke(listener, event);
    }

    public Method getMethod() {
        return method;
    }

    public Object getListener() {
        return listener;
    }
}
