package systems.reformcloud.reformcloud2.executor.api.common.language;

import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.language.language.Language;
import systems.reformcloud.reformcloud2.executor.api.common.language.language.source.LanguageSource;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public final class LanguageManager {

    private static final Map<LanguageSource, Language> languagePerSource = new HashMap<>();

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

    public static void reload(String defaultLanguage, Language... languages) {
        languagePerSource.clear();
        usageLanguage = null;
        load(defaultLanguage, languages);
    }

    public static String get(String key, Object... replacements) {
        return getOrDefault(key, "<message '" + key + "' missing>", replacements);
    }

    public static String getOrDefault(String key, String def, Object... replacements) {
        String message = usageLanguage.messages().getProperty(key, def);
        return MessageFormat.format(
                message,
                replacements
        );
    }
}
