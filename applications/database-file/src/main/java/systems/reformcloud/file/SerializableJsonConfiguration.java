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
package systems.reformcloud.file;

import de.derklaro.projects.deer.api.writer.FileWriter;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.configuration.JsonConfiguration;

import java.nio.file.Path;

public class SerializableJsonConfiguration extends JsonConfiguration implements FileWriter {

  public SerializableJsonConfiguration(JsonConfiguration configuration) {
    super(configuration.getBackingObject());
  }

  public static SerializableJsonConfiguration forPath(@NotNull Path path) {
    return new SerializableJsonConfiguration(JsonConfiguration.newJsonConfiguration(path));
  }

  @NotNull
  @Override
  public String toWriteableString() {
    return super.toPrettyString();
  }
}
