package de.klaro.reformcloud2.executor.api.common.utility.annotiations;

import java.lang.annotation.*;

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
