package systems.reformcloud.reformcloud2.executor.api.common.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Objects;

public final class Conditions {

    /**
     * Assumes that the given argument is true
     *
     * @param test    The argument which should be checked
     * @param message The message which will be printed if the argument is {@code false}
     */
    public static void isTrue(boolean test, @Nullable Object message) {
        if (!test) {
            throw new IllegalArgumentException(String.valueOf(message));
        }
    }

    /**
     * Assumes that the given argument is true
     *
     * @param test The argument which should be checked
     */
    public static void isTrue(boolean test) {
        isTrue(test, null);
    }

    /**
     * Assumes that the given argument is true
     *
     * @param test    The argument which should be checked
     * @param message The message which will be printed if the argument is {@code false}
     * @param args    The arguments which should be filled in in the message
     */
    public static void isTrue(boolean test, @NotNull String message, @Nullable Object... args) {
        if (!test) {
            throw new IllegalStateException(MessageFormat.format(message, args));
        }
    }

    /**
     * Checks if the given object is non-null
     *
     * @param obj The object which should get checked
     */
    public static void nonNull(@Nullable Object obj) {
        nonNull(obj, null);
    }

    /**
     * Checks if the given object is non-null
     *
     * @param obj     The object which should get checked
     * @param message The message which will be the stacktrace or {@code null}
     */
    public static void nonNull(@Nullable Object obj, @Nullable Object message) {
        Objects.requireNonNull(obj, String.valueOf(message));
    }
}
