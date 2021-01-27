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
package systems.reformcloud.reformcloud2.executor.api.configuration.json.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.Element;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface Object extends Element, Cloneable {

  @NotNull
  Optional<Element> remove(@NotNull String property);

  @NotNull
  Object add(@NotNull String property, @Nullable String value);

  @NotNull
  Object add(@NotNull String property, @Nullable Number value);

  @NotNull
  Object add(@NotNull String property, @Nullable Boolean value);

  @NotNull
  Object add(@NotNull String property, @Nullable Character value);

  @NotNull
  Object add(@NotNull String property, @Nullable Element value);

  @NotNull
  Optional<Element> get(@NotNull String property);

  boolean has(@NotNull String property);

  @NotNull
  Set<Map.Entry<String, Element>> entrySet();

  @NotNull
  Object clone();
}
