package de.klaro.reformcloud2.executor.api.common.language;

import de.klaro.reformcloud2.executor.api.common.language.loading.LanguageWorker;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class LanguageTest {

    @Test
    public void languageLoadTest() {
        LanguageWorker.doLoad();

        assertEquals(LanguageManager.get("language.setting.display"), "en-US");
        assertEquals(LanguageManager.get("command-unknown"), "The command {0} is not known! Please use \"help\" to get a list of all commands");
    }
}
