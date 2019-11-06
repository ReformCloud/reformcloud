package systems.reformcloud.reformcloud2.executor.api.common.base;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.MessageFormat;

public final class Conditions {

    /**
     * Assumes that the given argument is true
     *
     * @param test The argument which should be checked
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
     * @param test The argument which should be checked
     * @param message The message which will be printed if the argument is {@code false}
     * @param args The arguments which should be filled in in the message
     */
    public static void isTrue(boolean test, @Nonnull String message, @Nullable Object... args) {
        if (!test) {
            throw new IllegalStateException(MessageFormat.format(message, args));
        }
    }
}
