package systems.reformcloud.reformcloud2.executor.api.common.language;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import systems.reformcloud.reformcloud2.executor.api.common.language.loading.LanguageWorker;

public final class LanguageTest {

  @Test
  public void languageLoadTest() {
    LanguageWorker.doLoad();

    assertEquals(LanguageManager.get("language.setting.display"), "en_US");
    assertEquals(
        LanguageManager.get("command-unknown"),
        "The command {0} is not known! Please use \"help\" to get a list of all commands");
  }
}
