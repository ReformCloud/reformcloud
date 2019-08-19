package de.klaro.reformcloud2.executor.api.common.logger;

import de.klaro.reformcloud2.executor.api.common.logger.coloured.ColouredLoggerHandler;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public final class LoggerTest {

    @Test
    public void testLogger() throws IOException {
        LoggerBase loggerBase = new ColouredLoggerHandler();
        assertNotNull(loggerBase);
        System.out.println("&6Hallo");
    }
}
