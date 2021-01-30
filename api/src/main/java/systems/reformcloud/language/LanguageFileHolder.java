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
package systems.reformcloud.language;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import systems.reformcloud.utility.name.Nameable;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * A holder for a language file, computing the translations when needed.
 */
public interface LanguageFileHolder extends Nameable {

  /**
   * Creates a new language file holder with the given {@code properties} as the backing
   * translations holder.
   *
   * @param languageCode The language code of the language.
   * @param properties   The backing properties of the language.
   * @return The created holder.
   */
  @NotNull
  @Contract("_, _ -> new")
  static LanguageFileHolder properties(@NotNull String languageCode, @NotNull Properties properties) {
    return new PropertiesLanguageFileHolder(languageCode, properties);
  }

  /**
   * Get a specific translation by it's {@code key}.
   *
   * @param key The key of the translation.
   * @return The translation associated with the {@code key}.
   */
  @NotNull
  Optional<String> getTranslation(@NotNull String key);

  /**
   * Registers a translation to this holder if it is not present already.
   *
   * @param key         The key of the translation.
   * @param translation The translation associated with the key.
   * @return The same instance of this class, for chaining.
   */
  @NotNull
  LanguageFileHolder registerTranslation(@NotNull String key, @NotNull String translation);

  /**
   * Removes a translation from thus holder.
   *
   * @param key The key of the translation to remove.
   * @return The same instance of this class, for chaining.
   */
  @NotNull
  LanguageFileHolder unregisterTranslation(@NotNull String key);

  /**
   * Checks if a translation is associated with the given {@code key} in this holder.
   *
   * @param key The key of the translation to check.
   * @return If a translation with the given {@code key} is known to this holder.
   */
  boolean isTranslationPresent(@NotNull String key);

  /**
   * Get all translations registered in this holder.
   *
   * @return All translations registered in this holder.
   */
  @NotNull
  @Unmodifiable
  Map<String, String> getTranslations();
}
