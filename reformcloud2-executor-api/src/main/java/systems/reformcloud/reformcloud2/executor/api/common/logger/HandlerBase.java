package systems.reformcloud.reformcloud2.executor.api.common.logger;

import java.util.logging.Handler;
import javax.annotation.Nonnull;

public abstract class HandlerBase extends Handler {

  public HandlerBase(@Nonnull LoggerBase loggerBase) {
    this.loggerBase = loggerBase;
  }

  protected final LoggerBase loggerBase;
}
