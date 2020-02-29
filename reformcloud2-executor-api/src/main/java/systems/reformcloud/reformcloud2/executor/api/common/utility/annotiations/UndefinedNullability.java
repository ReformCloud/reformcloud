package systems.reformcloud.reformcloud2.executor.api.common.utility.annotiations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * Represents a method which can return undefined null values if the assigned value is null by the user
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value={METHOD})
public @interface UndefinedNullability {
}
