package systems.reformcloud.reformcloud2.executor.api.common.event.handler;

import java.lang.annotation.*;
import systems.reformcloud.reformcloud2.executor.api.common.event.priority.EventPriority;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Listener {

  EventPriority priority() default EventPriority.NORMAL;
}
