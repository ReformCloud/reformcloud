/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.reformcloud2.node.logger;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.LineReader;
import systems.reformcloud.reformcloud2.executor.api.event.EventManager;
import systems.reformcloud.reformcloud2.shared.io.IOUtils;
import systems.reformcloud.reformcloud2.node.NodeExecutor;
import systems.reformcloud.reformcloud2.node.event.logger.LogRecordProcessEvent;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class CloudLogger extends Logger {

    private final RecordDispatcher dispatcher = new RecordDispatcher(this);

    public CloudLogger(@NotNull LineReader lineReader) {
        super("CloudLogger", null);
        super.setLevel(Level.ALL);

        try {
            String logFile = System.getProperty("systems.reformcloud.console-log-file", "logs/cloud.log");
            IOUtils.createDirectory(Paths.get(logFile).getParent());

            FileHandler fileHandler = new FileHandler(logFile, 1 << 24, 8, true);
            fileHandler.setFormatter(new DefaultFormatter(false));
            fileHandler.setEncoding(StandardCharsets.UTF_8.name());
            super.addHandler(fileHandler);

            ColouredWriter colouredWriter = new ColouredWriter(lineReader);
            colouredWriter.setLevel(Level.parse(System.getProperty("systems.reformcloud.console-log-level", "ALL")));
            colouredWriter.setFormatter(new DefaultFormatter(true));
            colouredWriter.setEncoding(StandardCharsets.UTF_8.name());
            super.addHandler(colouredWriter);
        } catch (IOException exception) {
            System.err.println("Unable to prepare logger!");
            exception.printStackTrace();
        }

        System.setOut(new PrintStream(new LoggingOutputStream(this, Level.INFO), true));
        System.setErr(new PrintStream(new LoggingOutputStream(this, Level.SEVERE), true));

        this.dispatcher.start();
    }

    @Override
    public void log(LogRecord record) {
        this.dispatcher.queue(record);
    }

    protected void flushRecord(@NotNull LogRecord record) {
        if (this.preProcessRecord(record)) {
            super.log(record);
        }
    }

    public void close() throws InterruptedException {
        this.dispatcher.interrupt();
        this.dispatcher.join();
    }

    /**
     * Pre process the given log record and checks if the provided record should be logged
     * into the console or not.
     *
     * @param logRecord The log record to check
     * @return {@code true} if the record should be printed, {@code false} otherwise
     */
    @ApiStatus.AvailableSince("2.11.0-SNAPSHOT-SNAPSHOT")
    protected boolean preProcessRecord(@NotNull LogRecord logRecord) {
        return !NodeExecutor.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class)
            .callEvent(new LogRecordProcessEvent(logRecord))
            .isCanceled();
    }
}
