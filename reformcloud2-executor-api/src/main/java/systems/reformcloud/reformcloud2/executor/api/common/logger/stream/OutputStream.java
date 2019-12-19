package systems.reformcloud.reformcloud2.executor.api.common.logger.stream;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;
import systems.reformcloud.reformcloud2.executor.api.common.logger.OutputBase;

public final class OutputStream extends OutputBase {

  private static final String LINE_SEPARATOR =
      System.getProperty("line.separator");

  public OutputStream(LoggerBase loggerBase, Level level) {
    super(loggerBase);
    this.level = level;
  }

  private final Level level;

  @Override
  public void flush() throws IOException {
    synchronized (this) {
      super.flush();
      String content = toString(StandardCharsets.UTF_8.name());
      super.reset();
      if (!content.isEmpty() && !content.equals(LINE_SEPARATOR)) {
        loggerBase.logp(level, "", "", content);
      }
    }
  }
}
