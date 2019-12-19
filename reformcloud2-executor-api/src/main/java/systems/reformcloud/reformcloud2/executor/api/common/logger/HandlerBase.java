package systems.reformcloud.reformcloud2.executor.api.common.logger;

import javax.annotation.Nonnull;
import java.util.logging.Handler;

public abstract class HandlerBase extends Handler {

    public HandlerBase(@Nonnull LoggerBase loggerBase) {
        this.loggerBase = loggerBase;
    }

    protected final LoggerBase loggerBase;
}
