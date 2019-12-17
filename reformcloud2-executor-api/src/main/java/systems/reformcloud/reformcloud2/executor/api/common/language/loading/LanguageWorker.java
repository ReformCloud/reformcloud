package systems.reformcloud.reformcloud2.executor.api.common.language.loading;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.language.language.Language;
import systems.reformcloud.reformcloud2.executor.api.common.language.language.source.LanguageSource;
import systems.reformcloud.reformcloud2.executor.api.common.utility.function.Double;

public final class LanguageWorker {

  private LanguageWorker() {}

  public static void doLoad() {
    Double<String, LinkedList<Language>> in = detectLanguages();
    LanguageManager.load(in.getFirst(),
                         in.getSecond().toArray(new Language[0]));
  }

  public static void doReload() {
    Double<String, LinkedList<Language>> in = detectLanguages();
    LanguageManager.reload(in.getFirst(),
                           in.getSecond().toArray(new Language[0]));
  }

  private static Double<String, LinkedList<Language>> detectLanguages() {
    Double<String, LinkedList<Language>> done = null;

    try {
      LinkedList<Language> out = new LinkedList<>();
      AtomicReference<Language> atomicReference = new AtomicReference<>();

      Properties config = open("language-config.properties");
      String toSplit = config.getProperty("languages", "en,de");

      String[] languages =
          toSplit.contains(",") ? toSplit.split(",") : new String[] {toSplit};

      String defaultLang = config.getProperty("default-lang", "en");
      if (languages.length == 0) {
        throw new AssertionError("No languages found");
      }

      Arrays.stream(languages).forEach(s -> {
        try {
          Properties properties = open("languages/" + s + ".properties");
          LanguageSource languageSource =
              new InternalLanguageSource(properties);

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

      done = new Double<>(atomicReference.get().source().getSource(), out);
    } catch (final Throwable ex) {
      ex.printStackTrace();
    }

    return done;
  }

  private static Properties open(String path) throws IOException {
    Properties properties = new Properties();
    properties.load(
        LanguageWorker.class.getClassLoader().getResourceAsStream(path));
    return properties;
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

    @Nonnull
    @Override
    public String getName() {
      return display;
    }
  }
}
