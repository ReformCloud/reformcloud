package systems.reformcloud.reformcloud2.executor.api.configuration.gson;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JsonConfigurationTest {

    @Test
    void testFromString() {
        JsonConfiguration configuration = new JsonConfiguration("{\"test\": true, \"hi\": \"lol\", \"klaro\": null}");
        Assertions.assertTrue(configuration.getBoolean("test"));
        Assertions.assertEquals("lol", configuration.getString("hi"));
        Assertions.assertNull(configuration.get("klaro", Object.class));
    }

    @Test
    void testInvalidFromString() {
        JsonConfiguration configuration = new JsonConfiguration("{grg}");
        Assertions.assertTrue(configuration.getJsonObject().keySet().isEmpty());
    }

    @Test
    void testAdd() {
        JsonConfiguration configuration = new JsonConfiguration();
        configuration.add("test", "lol");

        Assertions.assertTrue(configuration.has("test"));
        Assertions.assertEquals("lol", configuration.getString("test"));
    }

    @Test
    void testToPrettyString() {
        JsonConfiguration configuration = new JsonConfiguration();
        Assertions.assertEquals("{}", configuration.toPrettyString());

        configuration.add("test", "lol");
        Assertions.assertEquals("{\n  \"test\": \"lol\"\n}", configuration.toPrettyString());
    }
}