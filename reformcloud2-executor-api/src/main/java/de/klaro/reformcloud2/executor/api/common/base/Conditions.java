package de.klaro.reformcloud2.executor.api.common.base;

import java.text.MessageFormat;

public final class Conditions {

    public static void isTrue(boolean test, Object message) {
        if (!test) {
            throw new IllegalArgumentException(String.valueOf(message));
        }
    }

    public static void isTrue(boolean test) {
        isTrue(test, (Object) null);
    }

    public static void isTrue(boolean test, String message, Object... args) {
        if (!test) {
            throw new IllegalStateException(MessageFormat.format(message, args));
        }
    }
}
