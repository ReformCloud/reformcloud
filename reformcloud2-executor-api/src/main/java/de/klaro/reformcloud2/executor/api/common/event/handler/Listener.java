package de.klaro.reformcloud2.executor.api.common.event.handler;

import de.klaro.reformcloud2.executor.api.common.event.priority.EventPriority;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Listener {

    EventPriority priority() default EventPriority.NORMAL;
}
