package systems.reformcloud.reformcloud2.executor.api.common.logger.coloured.handler;

import java.util.logging.LogRecord;
import systems.reformcloud.reformcloud2.executor.api.common.logger.HandlerBase;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;

public final class ColouredConsoleHandler extends HandlerBase {

  public ColouredConsoleHandler(LoggerBase loggerBase) { super(loggerBase); }

  @Override
  public void publish(LogRecord record) {
    if (isLoggable(record)) {
      loggerBase.log(getFormatter().format(record));
    }
  }

  @Override
  public void flush() {}

  @Override
  public void close() throws SecurityException {}
}
