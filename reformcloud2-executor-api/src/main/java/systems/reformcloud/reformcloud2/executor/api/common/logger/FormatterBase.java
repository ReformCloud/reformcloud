package systems.reformcloud.reformcloud2.executor.api.common.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import javax.annotation.Nonnull;

public abstract class FormatterBase extends Formatter {

  public FormatterBase(LoggerBase loggerBase) { this.loggerBase = loggerBase; }

  protected final LoggerBase loggerBase;

  /**
   * Formats an exception to a string writer
   *
   * @see Throwable
   * @see StringWriter
   *
   * @param throwable The throwable which should get converted
   * @return A string writer containing the stacktrace
   */
  @Nonnull
  protected StringWriter format(@Nonnull Throwable throwable) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    throwable.printStackTrace(printWriter);
    return stringWriter;
  }
}
