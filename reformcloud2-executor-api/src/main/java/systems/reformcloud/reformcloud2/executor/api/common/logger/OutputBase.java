package systems.reformcloud.reformcloud2.executor.api.common.logger;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;

public abstract class OutputBase extends ByteArrayOutputStream {

    public OutputBase(@Nonnull LoggerBase loggerBase) {
        this.loggerBase = loggerBase;
    }

    protected final LoggerBase loggerBase;
}
