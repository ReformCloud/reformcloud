package systems.reformcloud.reformcloud2.executor.api.common.language;

import org.junit.Before;
import org.junit.Test;
import systems.reformcloud.reformcloud2.executor.api.common.application.language.ApplicationLanguage;
import systems.reformcloud.reformcloud2.executor.api.common.language.loading.LanguageWorker;

import java.util.Properties;

import static org.junit.Assert.assertNotEquals;

public final class ApplicationLanguageLoadTest {

    @Before
    public void initLanguage() {
        LanguageWorker.doLoad();

        Properties properties = new Properties();
        properties.put("test", "value");
        LanguageManager.loadAddonMessageFile("test", new ApplicationLanguage("test", properties));
    }

    @Test
    public void testApplicationLanguage() {
        String message = LanguageManager.get("test");
        assertNotEquals("<message 'test' missing>", message);
    }
}
