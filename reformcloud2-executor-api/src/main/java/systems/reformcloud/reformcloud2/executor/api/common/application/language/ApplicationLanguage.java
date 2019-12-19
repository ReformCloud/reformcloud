package systems.reformcloud.reformcloud2.executor.api.common.application.language;

import systems.reformcloud.reformcloud2.executor.api.common.language.language.Language;
import systems.reformcloud.reformcloud2.executor.api.common.language.language.source.LanguageSource;

import javax.annotation.Nonnull;
import java.util.Properties;

public class ApplicationLanguage implements Language {

    public ApplicationLanguage(@Nonnull String addon, @Nonnull Properties properties) {
        this.source = new LanguageSource() {
            @Override
            public String getSource() {
                return addon;
            }

            @Nonnull
            @Override
            public String getName() {
                return addon;
            }
        };
        this.properties = properties;
    }

    private final LanguageSource source;

    private final Properties properties;

    @Override
    public LanguageSource source() {
        return source;
    }

    @Override
    public Properties messages() {
        return properties;
    }
}
