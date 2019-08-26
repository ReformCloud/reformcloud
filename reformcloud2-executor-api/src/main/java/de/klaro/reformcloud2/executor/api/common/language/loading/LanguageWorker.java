package de.klaro.reformcloud2.executor.api.common.language.loading;

import de.klaro.reformcloud2.executor.api.common.base.Conditions;
import de.klaro.reformcloud2.executor.api.common.language.LanguageManager;
import de.klaro.reformcloud2.executor.api.common.language.language.Language;
import de.klaro.reformcloud2.executor.api.common.language.language.source.LanguageSource;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Properties;

public final class LanguageWorker {

    private LanguageWorker() {}

    public static void doLoad() {
        try {
            LinkedList<Language> out = new LinkedList<>();
            Language use = null;
            Enumeration<URL> enumeration = LanguageWorker.class.getClassLoader().getResources("languages");
            while (enumeration.hasMoreElements()) {
                File file = new File(enumeration.nextElement().toURI());
                for (File languageFile : Objects.requireNonNull(file.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.isFile() && pathname.getName().endsWith(".properties");
                    }
                }))) {
                    try (InputStream inputStream = LanguageWorker.class.getClassLoader().getResourceAsStream("languages/" + languageFile.getName())) {
                        Properties properties = new Properties();
                        properties.load(inputStream);

                        if (properties.get("language.setting.name") == null || properties.get("language.setting.display") == null) {
                            throw new IllegalStateException("Language file which is not configured properly found");
                        }

                        LanguageSource languageSource = new InternalLanguageSource(properties);
                        Language language = new Language() {
                            @Override
                            public LanguageSource source() {
                                return languageSource;
                            }

                            @Override
                            public Properties messages() {
                                return properties;
                            }
                        };

                        if (file.getName().startsWith("use--") && use == null) {
                            use = language;
                        }

                        out.add(language);
                    }
                }
            }

            if (use == null) {
                Conditions.isTrue(out.size() != 0, "No language found");
                use = out.getFirst();
            }

            LanguageManager.load(use.source().getSource(), out.toArray(new Language[0]));
        } catch (final Throwable ex) {
            ex.printStackTrace();
        }
    }

    private static class InternalLanguageSource implements LanguageSource {

        InternalLanguageSource(Properties properties) {
            this.name = properties.getProperty("language.setting.name");
            this.display = properties.getProperty("language.setting.display");
        }

        private String name;

        private String display;

        @Override
        public String getSource() {
            return name;
        }

        @Override
        public String getName() {
            return display;
        }
    }
}
