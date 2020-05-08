/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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
package systems.reformcloud.reformcloud2.executor.api.common.language;

import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.language.language.Language;
import systems.reformcloud.reformcloud2.executor.api.common.language.language.source.LanguageSource;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class LanguageManager {

    private static final Map<LanguageSource, Language> languagePerSource = new HashMap<>();

    private static final Map<String, Language> languagePerAddon = new HashMap<>();

    private static Language usageLanguage;

    public static void load(String defaultLanguage, Language... languages) {
        for (Language language : languages) {
            if (!languagePerSource.containsKey(language.source())) {
                if (defaultLanguage.equals(language.source().getSource())) {
                    usageLanguage = language;
                }

                languagePerSource.put(language.source(), language);
            }
        }

        Conditions.isTrue(usageLanguage != null);
    }

    public static void loadAddonMessageFile(String addon, Language language) {
        languagePerAddon.put(addon, language);
    }

    public static void unregisterMessageFile(String addon) {
        languagePerAddon.remove(addon);
    }

    public static void reload(String defaultLanguage, Language... languages) {
        languagePerSource.clear();
        usageLanguage = null;
        load(defaultLanguage, languages);
    }

    public static String get(String key, Object... replacements) {
        return getOrDefault(key, "<message '" + key + "' missing>", replacements);
    }

    public static String getOrDefault(String key, String def, Object... replacements) {
        String message = usageLanguage.messages().getProperty(key);
        if (message == null) {
            List<Language> languages = languagePerAddon
                    .values()
                    .stream()
                    .filter(e -> e.messages().containsKey(key))
                    .collect(Collectors.toList());
            if (languages.isEmpty()) {
                message = def;
            } else {
                message = languages.get(0).messages().getProperty(key, def);
            }
        }

        Object[] strings = Arrays.stream(replacements).map(e -> e.toString()).toArray(String[]::new);
        return MessageFormat.format(
                message,
                strings
        );
    }
}
