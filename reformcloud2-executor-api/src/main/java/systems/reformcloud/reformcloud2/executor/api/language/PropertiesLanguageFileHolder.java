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
package systems.reformcloud.reformcloud2.executor.api.language;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class PropertiesLanguageFileHolder implements LanguageFileHolder {

    private final String languageCode;
    private final Map<String, String> translations;

    protected PropertiesLanguageFileHolder(String languageCode, Properties translations) {
        this.languageCode = languageCode;
        this.translations = new ConcurrentHashMap<>();
        copyTranslations(translations, this.translations);
    }

    @Override
    public @NotNull Optional<String> getTranslation(@NotNull String key) {
        return Optional.ofNullable(this.translations.get(key));
    }

    @Override
    public @NotNull LanguageFileHolder registerTranslation(@NotNull String key, @NotNull String translation) {
        this.translations.put(key, translation);
        return this;
    }

    @Override
    public @NotNull LanguageFileHolder unregisterTranslation(@NotNull String key, @NotNull String translation) {
        this.translations.remove(key);
        return this;
    }

    @Override
    @Unmodifiable
    public @NotNull Map<String, String> getTranslations() {
        return Map.copyOf(this.translations);
    }

    @Override
    public @NotNull String getName() {
        return this.languageCode;
    }

    private static void copyTranslations(@NotNull Properties source, @NotNull Map<String, String> target) {
        for (String stringPropertyName : source.stringPropertyNames()) {
            final String translation = source.getProperty(stringPropertyName);
            if (translation != null) {
                target.put(stringPropertyName, translation);
            }
        }
    }
}
