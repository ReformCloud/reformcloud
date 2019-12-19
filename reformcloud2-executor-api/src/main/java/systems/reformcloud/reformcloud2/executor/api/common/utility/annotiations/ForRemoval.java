package systems.reformcloud.reformcloud2.executor.api.common.utility.annotiations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * This class represents any method class constructor etc which will be removed
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value={CONSTRUCTOR, FIELD, LOCAL_VARIABLE, METHOD, PACKAGE, PARAMETER, TYPE})
public @interface ForRemoval {

    String reason() default "";

    boolean alreadyDeprecated() default false;

    String annotatedSince() default "09-18-19 12:00";
}
