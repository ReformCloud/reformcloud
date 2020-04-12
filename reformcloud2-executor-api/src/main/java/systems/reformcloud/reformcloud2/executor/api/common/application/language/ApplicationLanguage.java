package systems.reformcloud.reformcloud2.executor.api.common.application.language;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.language.language.Language;
import systems.reformcloud.reformcloud2.executor.api.common.language.language.source.LanguageSource;

import java.util.Properties;

public class ApplicationLanguage implements Language {

    public ApplicationLanguage(@NotNull String addon, @NotNull Properties properties) {
        this.source = new LanguageSource() {
            @Override
            public String getSource() {
                return addon;
            }

            @NotNull
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
