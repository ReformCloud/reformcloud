package systems.reformcloud.reformcloud2.executor.api.utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

class StringUtilTest {

    @Test
    void testGenerateString() {
        Assertions.assertEquals(64, StringUtil.generateString(2).length());
    }

    @Test
    void testReplaceLastEmpty() {
        Assertions.assertEquals(
            "1234 The fish 1234 is cooled",
            StringUtil.replaceLastEmpty("1234 The fish 1234 is cooled1234", "1234")
        );
    }

    @Test
    void testReplaceLast() {
        Assertions.assertEquals(
            "1234 The fish 1234 is cooled5",
            StringUtil.replaceLast("1234 The fish 1234 is cooled1234", "1234", "5")
        );
    }

    @Test
    void testCalcProperties() {
        Properties properties = StringUtil.calcProperties(new String[]{"test=true", "error=false", "lol=5", "mcn", "rz=main"}, 1);
        Assertions.assertEquals(3, properties.size());
        Assertions.assertEquals("false", properties.getProperty("error"));
        Assertions.assertEquals("5", properties.getProperty("lol"));
        Assertions.assertEquals("main", properties.getProperty("rz"));
    }
}
