package systems.reformcloud.reformcloud2.executor.api.common.configuration.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public final class InternalJsonParser {

    private InternalJsonParser() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public static JsonElement parseString(String json) throws JsonSyntaxException {
        return parseReader(new StringReader(json));
    }

    @Nonnull
    public static JsonElement parseReader(Reader reader) throws JsonSyntaxException {
        try (JsonReader jsonReader = new JsonReader(reader)) {
            JsonElement element = parseReader(jsonReader);

            if (!element.isJsonNull() && jsonReader.peek() != JsonToken.END_DOCUMENT) {
                throw new JsonSyntaxException("Did not consume the entire document.");
            } else {
                return element;
            }
        } catch (final MalformedJsonException | NumberFormatException ex) {
            throw new JsonSyntaxException(ex);
        } catch (final IOException ex) {
            throw new JsonIOException(ex);
        }
    }

    @Nonnull
    private static JsonElement parseReader(JsonReader reader) throws JsonIOException, JsonSyntaxException {
        final boolean lenient = reader.isLenient();
        reader.setLenient(true);

        JsonElement element;
        try {
            element = Streams.parse(reader);
        } catch (final StackOverflowError | OutOfMemoryError ex) {
            throw new JsonParseException("Failed parsing JSON source: " + reader + " to Json", ex);
        } finally {
            reader.setLenient(lenient);
        }

        return element;
    }
}
