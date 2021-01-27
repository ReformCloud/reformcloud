/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
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
package systems.reformcloud.reformcloud2.shared.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.JsonFactories;

public class GsonFactories extends JsonFactories {

  private GsonFactories() {
    throw new UnsupportedOperationException();
  }

  public static void init() {
    DEFAULT_PARSER.set(new GsonParser());
    BUILDER_FACTORY.set(GsonAdapterBuilder::new);
    OBJECT_FACTORY.set(() -> new GsonObject(new JsonObject()));
    ARRAY_FACTORY.set(() -> new GsonArray(new JsonArray()));
    NULL_FACTORY.set(() -> GsonNull.INSTANCE);
    PRIMITIVE_FACTORY.set(o -> {
      if (o instanceof Character) {
        return new GsonPrimitive(new JsonPrimitive((Character) o));
      } else if (o instanceof String) {
        return new GsonPrimitive(new JsonPrimitive((String) o));
      } else if (o instanceof Number) {
        return new GsonPrimitive(new JsonPrimitive((Number) o));
      } else if (o instanceof Boolean) {
        return new GsonPrimitive(new JsonPrimitive((Boolean) o));
      } else {
        throw new IllegalStateException("Unsupported object " + o.getClass().getName());
      }
    });
  }
}
