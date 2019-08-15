package de.klaro.reformcloud2.executor.api.common.base;

public final class Conditions {

    public static void isTrue(boolean test, Object message) {
        if (!test) {
            throw new IllegalArgumentException(String.valueOf(message));
        }
    }

    public static void isTrue(boolean test) {
        isTrue(test, null);
    }
}
