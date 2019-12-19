package systems.reformcloud.reformcloud2.executor.api.common.logger;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import org.junit.Test;
import systems.reformcloud.reformcloud2.executor.api.common.logger.coloured.ColouredLoggerHandler;

public final class LoggerTest {

  @Test
  public void testLogger() throws IOException {
    LoggerBase loggerBase = new ColouredLoggerHandler();
    assertNotNull(loggerBase);
    System.out.println("&6Hallo");

    // Close all handlers to delete the log file later
    for (Handler handler : loggerBase.getHandlers()) {
      handler.close();
    }
  }

  @Test
  public void deleteLogs() {
    LogManager.getLogManager().reset(); // Reset the log manager to reset all
                                        // loggers in the current jvm

    try {
      Files.deleteIfExists(
          Files.walkFileTree(Paths.get("logs"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(
                Path file, BasicFileAttributes attrs) throws IOException {
              Files.deleteIfExists(file);
              return FileVisitResult.CONTINUE;
            }
          }));
    } catch (final IOException ex) {
      ex.printStackTrace();
    }
  }
}
