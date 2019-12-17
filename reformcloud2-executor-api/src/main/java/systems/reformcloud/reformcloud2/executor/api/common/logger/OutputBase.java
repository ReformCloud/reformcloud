package systems.reformcloud.reformcloud2.executor.api.common.logger;

import java.io.ByteArrayOutputStream;
import javax.annotation.Nonnull;

public abstract class OutputBase extends ByteArrayOutputStream {

  public OutputBase(@Nonnull LoggerBase loggerBase) {
    this.loggerBase = loggerBase;
  }

  protected final LoggerBase loggerBase;
}
