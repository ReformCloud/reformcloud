package systems.reformcloud.reformcloud2.executor.api.utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JavaVersionTest {

    @Test
    void testIsCompatibleWith() {
        // runtime version -> compile version
        Assertions.assertTrue(JavaVersion.VERSION_1_8.isCompatibleWith(JavaVersion.VERSION_1_8));
        Assertions.assertTrue(JavaVersion.VERSION_16.isCompatibleWith(JavaVersion.VERSION_1_8));
        Assertions.assertFalse(JavaVersion.VERSION_1_8.isCompatibleWith(JavaVersion.VERSION_16));
    }

    @Test
    void testGetMajorVersion() {
        Assertions.assertSame(JavaVersion.VERSION_1_1, JavaVersion.getVersionForMajor(1));
        Assertions.assertSame(JavaVersion.VERSION_1_8, JavaVersion.getVersionForMajor(8));
        Assertions.assertSame(JavaVersion.VERSION_16, JavaVersion.getVersionForMajor(16));
        Assertions.assertSame(JavaVersion.VERSION_UNKNOWN, JavaVersion.getVersionForMajor(-1));
        Assertions.assertSame(JavaVersion.VERSION_UNKNOWN, JavaVersion.getVersionForMajor(JavaVersion.values().length + 10));
    }

    @Test
    void testCurrent() {
        Assertions.assertNotSame(JavaVersion.VERSION_UNKNOWN, JavaVersion.current());
        Assertions.assertTrue(JavaVersion.current().ordinal() >= JavaVersion.VERSION_1_8.ordinal());
    }

    @Test
    void testForClassVersion() {
        // See https://en.wikipedia.org/wiki/Java_class_file#General_layout
        Assertions.assertSame(JavaVersion.VERSION_1_1, JavaVersion.forClassVersion(45));
        Assertions.assertSame(JavaVersion.VERSION_1_8, JavaVersion.forClassVersion(52));
        Assertions.assertSame(JavaVersion.VERSION_11, JavaVersion.forClassVersion(55));
        Assertions.assertSame(JavaVersion.VERSION_15, JavaVersion.forClassVersion(59));
    }
}
