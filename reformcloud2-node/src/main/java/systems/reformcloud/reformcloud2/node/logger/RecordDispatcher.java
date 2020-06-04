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
package systems.reformcloud.reformcloud2.node.logger;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.LogRecord;

public class RecordDispatcher extends Thread {

    private final CloudLogger logger;
    private final BlockingQueue<LogRecord> queue = new LinkedBlockingQueue<>();

    public RecordDispatcher(CloudLogger logger) {
        super("Cloud logging thread");
        this.logger = logger;
    }

    @Override
    public void run() {
        while (!super.isInterrupted()) {
            try {
                this.logger.doLog(this.queue.take());
            } catch (InterruptedException ignored) {
            }
        }

        for (LogRecord logRecord : this.queue) {
            this.logger.doLog(logRecord);
        }
    }

    void queue(@NotNull LogRecord record) {
        if (!super.isInterrupted()) {
            this.queue.add(record);
        }
    }
}
