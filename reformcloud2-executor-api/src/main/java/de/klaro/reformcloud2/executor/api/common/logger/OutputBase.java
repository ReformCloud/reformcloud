package de.klaro.reformcloud2.executor.api.common.logger;

import java.io.ByteArrayOutputStream;

public abstract class OutputBase extends ByteArrayOutputStream {

    public OutputBase(LoggerBase loggerBase) {
        this.loggerBase = loggerBase;
    }

    protected final LoggerBase loggerBase;
}
