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
package systems.reformcloud.reformcloud2.executor.api.language.loading;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.language.Language;
import systems.reformcloud.reformcloud2.executor.api.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.language.LanguageSource;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Duo;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

public final class LanguageLoader {

    private LanguageLoader() {
    }

    public static void doLoad() {
        Duo<String, LinkedList<Language>> in = detectLanguages();
        LanguageManager.load(in.getFirst(), in.getSecond().toArray(new Language[0]));
    }

    public static void doReload() {
        Duo<String, LinkedList<Language>> in = detectLanguages();
        LanguageManager.reload(in.getFirst(), in.getSecond().toArray(new Language[0]));
    }

    private static Duo<String, LinkedList<Language>> detectLanguages() {
        Duo<String, LinkedList<Language>> done = null;

        try {
            LinkedList<Language> out = new LinkedList<>();
            AtomicReference<Language> atomicReference = new AtomicReference<>();

            Properties config = open("language-config.properties");
            String toSplit = config.getProperty("languages", "en,de");

            String[] languages = toSplit.contains(",") ? toSplit.split(",") : new String[]{toSplit};

            String defaultLang = config.getProperty("default-lang", "en");
            if (languages.length == 0) {
                throw new AssertionError("No languages found");
            }

            Arrays.stream(languages).forEach(s -> {
                try {
                    Properties properties = open("languages/" + s + ".properties");
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
                    out.add(language);
                    if (s.equals(defaultLang)) {
                        atomicReference.set(language);
                    }
                } catch (final IOException ex) {
                    ex.printStackTrace();
                }
            });

            if (atomicReference.get() == null) {
                Conditions.isTrue(out.size() != 0, "No language found");
                atomicReference.set(out.getFirst());
            }

            done = new Duo<>(atomicReference.get().source().getSource(), out);
        } catch (final Throwable ex) {
            ex.printStackTrace();
        }

        return done;
    }

    private static Properties open(String path) throws IOException {
        Properties properties = new Properties();
        properties.load(LanguageLoader.class.getClassLoader().getResourceAsStream(path));
        return properties;
    }

    private static class InternalLanguageSource implements LanguageSource {

        private final String name;
        private final String display;

        private InternalLanguageSource(Properties properties) {
            this.name = properties.getProperty("language.setting.name");
            this.display = properties.getProperty("language.setting.display");
        }

        @Override
        public String getSource() {
            return this.name;
        }

        @NotNull
        @Override
        public String getName() {
            return this.display;
        }
    }

    @ApiStatus.Internal
    public static class InternalLanguage implements Language {

        private final LanguageSource source;
        private final Properties properties;

        public InternalLanguage(Properties properties) {
            this.properties = properties;
            this.source = new InternalLanguageSource(properties);
        }

        @Override
        public LanguageSource source() {
            return this.source;
        }

        @Override
        public Properties messages() {
            return this.properties;
        }
    }
}
