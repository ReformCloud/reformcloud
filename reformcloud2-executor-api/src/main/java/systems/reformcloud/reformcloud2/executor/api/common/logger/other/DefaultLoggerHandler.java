package systems.reformcloud.reformcloud2.executor.api.common.logger.other;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import jline.console.ConsoleReader;
import org.fusesource.jansi.Ansi;
import systems.reformcloud.reformcloud2.executor.api.common.logger.Debugger;
import systems.reformcloud.reformcloud2.executor.api.common.logger.HandlerBase;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerLineHandler;
import systems.reformcloud.reformcloud2.executor.api.common.logger.coloured.formatter.LogFileFormatter;
import systems.reformcloud.reformcloud2.executor.api.common.logger.other.debugger.DefaultDebugger;
import systems.reformcloud.reformcloud2.executor.api.common.logger.other.fornatter.DefaultLogFormatter;
import systems.reformcloud.reformcloud2.executor.api.common.logger.other.handler.DefaultConsoleHandler;
import systems.reformcloud.reformcloud2.executor.api.common.logger.stream.OutputStream;

public final class DefaultLoggerHandler extends LoggerBase {

  private static final String PROMPT =
      System.getProperty("user.name") + "@ReformCloud2~# ";

  public DefaultLoggerHandler() throws IOException {
    this.consoleReader = new ConsoleReader(System.in, System.out);
    this.consoleReader.setExpandEvents(false);

    FileHandler fileHandler =
        new FileHandler("logs/cloud.log", 70000000, 8, true);
    fileHandler.setLevel(Level.ALL);
    fileHandler.setFormatter(new LogFileFormatter(this));
    addHandler(fileHandler);

    HandlerBase consoleHandler = new DefaultConsoleHandler(this);
    consoleHandler.setLevel(Level.ALL);
    consoleHandler.setFormatter(new DefaultLogFormatter(this));
    addHandler(consoleHandler);

    System.setOut(new PrintStream(new OutputStream(this, Level.INFO), true));
    System.setErr(new PrintStream(new OutputStream(this, Level.SEVERE), true));
  }

  private final ConsoleReader consoleReader;

  private final Debugger debugger = new DefaultDebugger();

  private final List<LoggerLineHandler> handlers = new ArrayList<>();

  @Nonnull
  @Override
  public ConsoleReader getConsoleReader() {
    return consoleReader;
  }

  @Nonnull
  @Override
  public String readLine() {
    try {
      return consoleReader.readLine(PROMPT);
    } catch (final IOException ex) {
      ex.printStackTrace();
    }

    return "";
  }

  @Nonnull
  @Override
  public String readLineNoPrompt() {
    try {
      return consoleReader.readLine();
    } catch (final IOException ex) {
      ex.printStackTrace();
    }

    return "";
  }

  @Nonnull
  @Override
  public String readString(@Nonnull Predicate<String> predicate,
                           @Nonnull Runnable invalidInputMessage) {
    String line = readLine();
    while (!predicate.test(line)) {
      invalidInputMessage.run();
      line = readLine();
    }

    return line;
  }

  @Nonnull
  @Override
  public <T> T read(@Nonnull Function<String, T> function,
                    @Nonnull Runnable invalidInputMessage) {
    String line = readLine();
    T result;
    while ((result = function.apply(line)) == null) {
      invalidInputMessage.run();
      line = readLine();
    }

    return result;
  }

  @Override
  public void log(@Nonnull String message) {
    handleLine(message);

    try {
      consoleReader.print(Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() +
                          ConsoleReader.RESET_LINE + message +
                          Ansi.ansi().reset().toString());
      consoleReader.drawLine();
      consoleReader.flush();
    } catch (final IOException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void logRaw(@Nonnull String message) {
    handleLine(message);

    try {
      consoleReader.print(message + Ansi.ansi().reset().toString());
      consoleReader.drawLine();
      consoleReader.flush();
    } catch (final IOException ex) {
      ex.printStackTrace();
    }
  }

  @Nonnull
  @Override
  public LoggerBase addLogLineHandler(@Nonnull LoggerLineHandler handler) {
    handlers.add(handler);
    return this;
  }

  @Nonnull
  @Override
  public Debugger getDebugger() {
    return debugger;
  }

  @Override
  public void close() throws Exception {
    consoleReader.drawLine();
    consoleReader.flush();
    consoleReader.close();
  }

  private void handleLine(String line) {
    handlers.forEach(handler -> {
      handler.handleLine(line, DefaultLoggerHandler.this);
      handler.handleRaw(
          line, DefaultLoggerHandler
                    .this); // Line is always raw because no colour is used
    });
  }
}
