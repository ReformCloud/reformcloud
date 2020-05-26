/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.api.logger;

import org.junit.Before;
import org.junit.Test;
import systems.reformcloud.reformcloud2.executor.api.commands.basic.manager.DefaultCommandManager;
import systems.reformcloud.reformcloud2.executor.api.language.loading.LanguageWorker;
import systems.reformcloud.reformcloud2.executor.api.logger.coloured.ColouredLoggerHandler;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Handler;
import java.util.logging.LogManager;

import static org.junit.Assert.assertNotNull;

public final class LoggerTest {

    @Before
    public void initEnv() {
        LanguageWorker.doLoad();
    }

    @Test
    public void testLogger() throws IOException {
        LoggerBase loggerBase = new ColouredLoggerHandler(new DefaultCommandManager());
        assertNotNull(loggerBase);
        System.out.println("&6Hallo");

        //Close all handlers to delete the log file later
        for (Handler handler : loggerBase.getHandlers()) {
            handler.close();
        }
    }

    @Test
    public void deleteLogs() {
        LogManager.getLogManager().reset(); //Reset the log manager to reset all loggers in the current jvm

        try {
            Files.deleteIfExists(Files.walkFileTree(Paths.get("logs"), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.deleteIfExists(file);
                    return FileVisitResult.CONTINUE;
                }
            }));
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}
