package systems.reformcloud.reformcloud2.executor.api.configuration.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InternalJsonParserTest {

    @Test
    void testParseString() {
        JsonElement element = InternalJsonParser.parseString("{\"test\": true, \"hi\": \"lol\", \"klaro\": null}");
        Assertions.assertNotNull(element);
    }

    @Test
    void testParseInvalidString() {
        Assertions.assertThrows(JsonSyntaxException.class, () -> InternalJsonParser.parseString("{derklaro}"));
    }
}