package de.klaro.reformcloud2.executor.api.common.logger;

import java.util.logging.Handler;

public abstract class HandlerBase extends Handler {

    public HandlerBase(LoggerBase loggerBase) {
        this.loggerBase = loggerBase;
    }

    protected final LoggerBase loggerBase;
}
