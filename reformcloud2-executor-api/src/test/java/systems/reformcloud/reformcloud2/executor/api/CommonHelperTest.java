package systems.reformcloud.reformcloud2.executor.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Version;

import java.util.UUID;

class CommonHelperTest {

    @Test
    void testFromString() {
        Integer i = CommonHelper.fromString("1");
        Assertions.assertNotNull(i);
        Assertions.assertEquals(1, i);

        Integer i1 = CommonHelper.fromString("lol");
        Assertions.assertNull(i1);
    }

    @Test
    void testBooleanFromString() {
        Boolean b = CommonHelper.booleanFromString("true");
        Assertions.assertNotNull(b);
        Assertions.assertTrue(b);

        Boolean b1 = CommonHelper.booleanFromString("xD");
        Assertions.assertNull(b1);
    }

    @Test
    void testLongFromString() {
        Long l = CommonHelper.longFromString("1597599136443");
        Assertions.assertNotNull(l);
        Assertions.assertEquals(1597599136443L, l);

        Long l1 = CommonHelper.longFromString("554vrg445");
        Assertions.assertNull(l1);
    }

    @Test
    void testTryParse() {
        UUID uuid = CommonHelper.tryParse("bcc582ed-494d-4b93-86cb-b58564651a26");
        Assertions.assertNotNull(uuid);
        Assertions.assertEquals(UUID.fromString("bcc582ed-494d-4b93-86cb-b58564651a26"), uuid);

        UUID uuid1 = CommonHelper.tryParse("luuul");
        Assertions.assertNull(uuid1);
    }

    @Test
    void testLongToInt() {
        int result = CommonHelper.longToInt(1597599136443L);
        Assertions.assertEquals(Integer.MAX_VALUE, result);

        int result1 = CommonHelper.longToInt(1L);
        Assertions.assertEquals(1, result1);
    }

    @Test
    void testSetSystemPropertyIfUnset() {
        System.setProperty("test", "test1");

        CommonHelper.setSystemPropertyIfUnset("test", "test2");
        Assertions.assertEquals("test1", System.getProperty("test"));

        CommonHelper.setSystemPropertyIfUnset("test1", "test1");
        Assertions.assertEquals("test1", System.getProperty("test1"));
    }

    @Test
    void testFormatThrowable() {
        String expected = "NullPointerException : null @ systems.reformcloud.reformcloud2.executor.api.CommonHelperTest:74";
        Assertions.assertEquals(expected, CommonHelper.formatThrowable(new NullPointerException()));
    }

    @Test
    void testFindEnumField() {
        Version version = CommonHelper.findEnumField(Version.class, "paper_1_8_8").orNothing();
        Assertions.assertNotNull(version);
        Assertions.assertEquals(Version.PAPER_1_8_8, version);

        Version version1 = CommonHelper.findEnumField(Version.class, "paper_1_4_9").orNothing();
        Assertions.assertNull(version1);
    }
}