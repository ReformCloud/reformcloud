package de.klaro.reformcloud2.executor.api.common.logger.coloured.handler;

import de.klaro.reformcloud2.executor.api.common.logger.HandlerBase;
import de.klaro.reformcloud2.executor.api.common.logger.LoggerBase;

import java.util.logging.LogRecord;

public final class ColouredConsoleHandler extends HandlerBase {

    public ColouredConsoleHandler(LoggerBase loggerBase) {
        super(loggerBase);
    }

    @Override
    public void publish(LogRecord record) {
        if (isLoggable(record)) {
            loggerBase.log(getFormatter().format(record));
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}
