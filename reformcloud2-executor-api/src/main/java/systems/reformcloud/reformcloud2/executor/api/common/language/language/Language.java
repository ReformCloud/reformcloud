package systems.reformcloud.reformcloud2.executor.api.common.language.language;

import systems.reformcloud.reformcloud2.executor.api.common.language.language.source.LanguageSource;

import java.util.Properties;

public interface Language {

    /**
     * @return The source of the current language
     */
    LanguageSource source();

    /**
     * @return The messages of the current language in a {@link Properties}-file
     */
    Properties messages();
}
