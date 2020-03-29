package systems.reformcloud.reformcloud2.executor.api.common.logger;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;

public abstract class OutputBase extends ByteArrayOutputStream {

    public OutputBase(@NotNull LoggerBase loggerBase) {
        this.loggerBase = loggerBase;
    }

    protected final LoggerBase loggerBase;
}
