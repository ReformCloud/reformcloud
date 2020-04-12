package systems.reformcloud.reformcloud2.executor.api.common.logger;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Handler;

public abstract class HandlerBase extends Handler {

    public HandlerBase(@NotNull LoggerBase loggerBase) {
        this.loggerBase = loggerBase;
    }

    protected final LoggerBase loggerBase;
}
