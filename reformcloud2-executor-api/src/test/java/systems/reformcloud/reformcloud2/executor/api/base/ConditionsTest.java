package systems.reformcloud.reformcloud2.executor.api.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConditionsTest {

    @Test
    void testIsTrue() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Conditions.isTrue(false));
    }

    @Test
    void testNonNull() {
        Assertions.assertThrows(NullPointerException.class, () -> Conditions.nonNull(null));
    }
}