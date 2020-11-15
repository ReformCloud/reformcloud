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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public final class TranslationHolder {

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final AtomicReference<String> IN_USE_LANGUAGE_CODE = new AtomicReference<>();
    private static final Map<String, LanguageFileHolder> LOADED_LANGUAGES = new ConcurrentHashMap<>();

    public static boolean registerLanguageFileHolder(@NotNull LanguageFileHolder holder) {
        return LOADED_LANGUAGES.putIfAbsent(holder.getName(), holder) == null;
    }

    @NotNull
    public static Optional<LanguageFileHolder> unregisterLanguageFileHolder(@NotNull String name) {
        return Optional.ofNullable(LOADED_LANGUAGES.remove(name));
    }

    @NotNull
    public static String translate(@NotNull String key, @NonNls Object... replacements) {
        return translate(key, "<message '" + key + "' missing>", replacements);
    }

    @NotNull
    public static String getEnabledLanguage() {
        String result = IN_USE_LANGUAGE_CODE.get();
        if (result == null) {
            detectAndLoadFiles();
            result = IN_USE_LANGUAGE_CODE.get();
        }
        return result;
    }

    public static void setEnabledLanguage(@NotNull String languageCode) {
        if (LOADED_LANGUAGES.containsKey(languageCode)) {
            IN_USE_LANGUAGE_CODE.set(languageCode);
        } else {
            throw new IllegalStateException("For language code \"" + languageCode + "\" are no messages present");
        }
    }

    @NotNull
    @Unmodifiable
    public static Map<String, LanguageFileHolder> getLoadedLanguages() {
        return Map.copyOf(LOADED_LANGUAGES);
    }

    @NotNull
    public static String translate(@NotNull String key, @NotNull String def, @NonNls Object... replacements) {
        String languageCode = IN_USE_LANGUAGE_CODE.get();
        if (languageCode == null) {
            detectAndLoadFiles();
            languageCode = IN_USE_LANGUAGE_CODE.get();
        }

        final LanguageFileHolder holder = LOADED_LANGUAGES.get(languageCode);
        if (holder == null) {
            throw new IllegalStateException("Enabled language code \"" + languageCode + "\" has no translations present");
        }

        return MessageFormat.format(holder.getTranslation(key).orElse(def), getArgs(replacements));
    }

    @NotNull
    @Contract(pure = true)
    private static Object[] getArgs(@NonNls Object... replacements) {
        if (replacements.length > 0) {
            final Object[] result = new Object[replacements.length];
            for (int i = 0; i < replacements.length; i++) {
                result[i] = replacements[i].toString();
            }
            return result;
        }
        return EMPTY_OBJECT_ARRAY;
    }

    private static void detectAndLoadFiles() {
        final String languageToUse = System.getProperty("systems.reformcloud.language-code", "en");
        try (InputStream stream = TranslationHolder.class.getClassLoader().getResourceAsStream("languages/" + languageToUse)) {
            if (stream == null) {
                throw new IllegalStateException("Invalid language code \"" + languageToUse + "\" specified");
            }
            Properties properties = new Properties();
            properties.load(stream);
            registerLanguageFileHolder(LanguageFileHolder.properties(languageToUse, properties));
            IN_USE_LANGUAGE_CODE.set(languageToUse);
        } catch (IOException exception) {
            throw new RuntimeException("Unable to load language file \"" + languageToUse + "\"", exception);
        }
    }
}
