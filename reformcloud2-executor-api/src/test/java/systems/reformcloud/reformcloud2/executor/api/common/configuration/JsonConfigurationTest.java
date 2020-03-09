package systems.reformcloud.reformcloud2.executor.api.common.configuration;

import org.junit.Test;

import static org.junit.Assert.*;

public class JsonConfigurationTest {

    @Test
    public void testCreate() {
        JsonConfiguration configuration = new JsonConfiguration();
        assertNotNull(configuration.getJsonObject());
    }

    @Test
    public void testSetAndGet() {
        JsonConfiguration configuration = new JsonConfiguration();

        configuration.add("test", "test");
        assertEquals("test", configuration.getString("test"));

        configuration.add("test2", 0);
        assertTrue(configuration.has("test2"));

        configuration.remove("test2");
        assertFalse(configuration.has("test2"));

        assertEquals("hello", configuration.getOrDefault("test3", "hello"));
        assertEquals("hello2", configuration.getOrDefaultIf("test4", "hello2", x -> true));
    }
}
