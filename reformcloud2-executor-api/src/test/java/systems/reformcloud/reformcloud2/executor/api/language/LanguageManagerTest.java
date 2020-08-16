package systems.reformcloud.reformcloud2.executor.api.language;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import systems.reformcloud.reformcloud2.executor.api.language.loading.LanguageLoader;

class LanguageManagerTest {

    @Test
    void testLoadLanguages() {
        LanguageLoader.doLoad();
        Assertions.assertNotNull(LanguageManager.get("vvv"));
        Assertions.assertNotNull(LanguageManager.getOrDefault("language.setting.display", null));
        Assertions.assertEquals("<message test missing>", LanguageManager.get("test"));
    }
}