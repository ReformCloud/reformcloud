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
package systems.reformcloud.reformcloud2.node.argument;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.shared.StringUtil;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

public class DefaultArgumentParser implements ArgumentParser {

  private final Properties properties;

  public DefaultArgumentParser(@NonNls String[] args) {
    this.properties = StringUtil.parseProperties(args, 0);
  }

  @Override
  public boolean has(@NotNull String key) {
    return this.properties.containsKey(key);
  }

  @Override
  public @NotNull Optional<String> getArgumentRaw(@NotNull String key) {
    return Optional.ofNullable(this.properties.getProperty(key));
  }

  @Override
  public boolean getBoolean(@NotNull String key) {
    return this.get(key, Boolean::parseBoolean, null).orElse(false);
  }

  @Override
  public int getInt(@NotNull String key) {
    return this.get(key, Integer::parseInt, null).orElse(0);
  }

  @Override
  public long getLong(@NotNull String key) {
    return this.get(key, Long::parseLong, null).orElse(0L);
  }

  @Override
  public float getFloat(@NotNull String key) {
    return this.get(key, Float::parseFloat, null).orElse(0F);
  }

  @Override
  public double getDouble(@NotNull String key) {
    return this.get(key, Double::parseDouble, null).orElse(0D);
  }

  @Override
  public @NotNull <T> Optional<T> get(@NotNull String key, @NotNull Function<String, T> function, @Nullable T defaultValue) {
    try {
      String value = this.properties.getProperty(key);
      if (value == null) {
        return Optional.ofNullable(defaultValue);
      }

      return Optional.ofNullable(function.apply(value));
    } catch (Throwable throwable) {
      return Optional.ofNullable(defaultValue);
    }
  }
}
