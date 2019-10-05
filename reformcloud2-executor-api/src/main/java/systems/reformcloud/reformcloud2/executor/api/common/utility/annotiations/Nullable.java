package systems.reformcloud.reformcloud2.executor.api.common.utility.annotiations;

import java.lang.annotation.*;

/**
 * This class represents any variable method etc which can return null
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(
        {
                ElementType.METHOD,
                ElementType.FIELD,
                ElementType.PARAMETER,
                ElementType.LOCAL_VARIABLE
        }
)
public @interface Nullable {
}
