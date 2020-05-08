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
package systems.reformcloud.reformcloud2.executor.node.process.log;

import systems.reformcloud.reformcloud2.executor.api.common.process.running.manager.SharedRunningProcessManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class LogLineReader extends AbsoluteThread {

    public LogLineReader() {
        enableDaemon().updatePriority(Thread.MIN_PRIORITY).start();
    }

    private final StringBuffer stringBuffer = new StringBuffer();

    private final byte[] buffer = new byte[1024];

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            SharedRunningProcessManager.getAllProcesses().stream().filter(e -> e.getProcess().isPresent()).forEach(e -> {
                InputStream inputStream = e.getProcess().get().getInputStream();
                try {
                    int len;
                    while (inputStream.available() > 0 && (len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                        stringBuffer.append(new String(buffer, 0, len, StandardCharsets.UTF_8));
                    }

                    String text = stringBuffer.toString();
                    if (!text.contains("\n") && !text.contains("\r")) {
                        stringBuffer.setLength(0);
                        return;
                    }

                    NodeProcessScreenHandler.getScreen(e.getProcessInformation().getProcessDetail().getProcessUniqueID()).ifPresent(s -> {
                        for (String in : text.split("\r")) {
                            for (String string : in.split("\n")) {
                                if (!string.trim().isEmpty()) {
                                    s.addScreenLine(string);
                                }
                            }
                        }
                    });

                    stringBuffer.setLength(0);
                } catch (final Throwable ignored) {
                    stringBuffer.setLength(0);
                }
            });

            AbsoluteThread.sleep(TimeUnit.MILLISECONDS, 50);
        }
    }
}
