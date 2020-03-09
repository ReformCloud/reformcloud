package systems.reformcloud.reformcloud2.executor.api.common.configuration.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;

import java.io.IOException;

public class JsonConfigurationTypeAdapter extends TypeAdapter<JsonConfiguration> {

    @Override
    public void write(JsonWriter jsonWriter, JsonConfiguration jsonConfiguration) throws IOException {
        TypeAdapters.JSON_ELEMENT.write(jsonWriter, jsonConfiguration == null ? new JsonObject() : jsonConfiguration.getJsonObject());
    }

    @Override
    public JsonConfiguration read(JsonReader jsonReader) throws IOException {
        JsonElement jsonElement = TypeAdapters.JSON_ELEMENT.read(jsonReader);
        if (jsonElement != null && jsonElement.isJsonObject()) {
            return new JsonConfiguration(jsonElement.getAsJsonObject());
        } else {
            return null;
        }
    }
}
