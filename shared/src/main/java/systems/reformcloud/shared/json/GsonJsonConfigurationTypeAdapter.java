/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.shared.json;

import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import systems.reformcloud.configuration.JsonConfiguration;
import systems.reformcloud.configuration.json.types.Object;

import java.io.IOException;

final class GsonJsonConfigurationTypeAdapter extends TypeAdapter<JsonConfiguration> {

  @Override
  public void write(JsonWriter out, JsonConfiguration value) throws IOException {
    final Object backing = value.getBackingObject();
    if (backing instanceof GsonObject) {
      TypeAdapters.JSON_ELEMENT.write(out, ((GsonObject) backing).gsonObject);
    }
  }

  @Override
  public JsonConfiguration read(JsonReader in) throws IOException {
    final JsonElement element = TypeAdapters.JSON_ELEMENT.read(in);
    if (element.isJsonObject()) {
      return JsonConfiguration.newJsonConfiguration(new GsonObject(element.getAsJsonObject()));
    } else {
      return null;
    }
  }
}
