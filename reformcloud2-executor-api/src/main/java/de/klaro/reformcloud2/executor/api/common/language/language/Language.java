package de.klaro.reformcloud2.executor.api.common.language.language;

import de.klaro.reformcloud2.executor.api.common.language.language.source.LanguageSource;

import java.util.Properties;

public interface Language {

    LanguageSource source();

    Properties messages();
}
