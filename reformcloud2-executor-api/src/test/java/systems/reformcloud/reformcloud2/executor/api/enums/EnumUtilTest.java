package systems.reformcloud.reformcloud2.executor.api.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Version;

import java.util.EnumSet;

class EnumUtilTest {

    @Test
    void testFindEnumFieldByName() {
        Version version = EnumUtil.findEnumFieldByName(Version.class, "paper_1_8_8").orElse(null);
        Assertions.assertNotNull(version);
        Assertions.assertSame(Version.PAPER_1_8_8, version);
    }

    @Test
    void testFindEnumFieldByIndex() {
        Version version = EnumUtil.findEnumFieldByIndex(Version.class, Version.PAPER_1_8_8.ordinal()).orElse(null);
        Assertions.assertNotNull(version);
        Assertions.assertSame(Version.PAPER_1_8_8, version);
    }

    @Test
    void testGetEnumEntries() {
        EnumSet<Version> enumSet = EnumUtil.getEnumEntries(Version.class);
        Assertions.assertEquals(Version.values().length, enumSet.size());
    }
}
