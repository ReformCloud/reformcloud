package systems.reformcloud.reformcloud2.executor.api.common.language.language;

import systems.reformcloud.reformcloud2.executor.api.common.language.language.source.LanguageSource;

import java.util.Properties;

public interface Language {

    LanguageSource source();

    Properties messages();
}
