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
package systems.reformcloud.reformcloud2.executor.api.common.process.running.screen;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.events.ProcessScreenEnabledEvent;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.events.ProcessScreenEntryCachedEvent;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultRunningProcessScreen implements RunningProcessScreen {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private final Queue<String> recentScreenLines = new ConcurrentLinkedQueue<>();

    private final Collection<String> enabledReceivers = new CopyOnWriteArrayList<>();

    private final byte[] buffer = new byte[1024];

    private final StringBuffer stringBuffer = new StringBuffer();

    public DefaultRunningProcessScreen(@NotNull RunningProcess runningProcess) {
        this.runningProcess = runningProcess;
    }

    private final RunningProcess runningProcess;

    @Override
    public void callUpdate() {
        if (runningProcess.isAlive()) {
            runningProcess.getProcess().ifPresent(process -> {
                this.readStream(process.getInputStream());
                this.readStream(process.getErrorStream());
            });
        }
    }

    @NotNull
    @Override
    public Queue<String> getLastLogLines() {
        return this.recentScreenLines;
    }

    @NotNull
    @Override
    public RunningProcess getTargetProcess() {
        return this.runningProcess;
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<String> getReceivers() {
        return Collections.unmodifiableCollection(this.enabledReceivers);
    }

    @Override
    public void enableScreen(@NotNull String receiver) {
        this.enabledReceivers.add(receiver);
        ExecutorAPI.getInstance().getEventManager().callEvent(new ProcessScreenEnabledEvent(this, receiver));
    }

    @Override
    public void disableScreen(@NotNull String receiver) {
        this.enabledReceivers.remove(receiver);
    }

    @Override
    public boolean isEnabledFor(@NotNull String receiver) {
        return this.enabledReceivers.contains(receiver);
    }

    private synchronized void readStream(@NotNull InputStream inputStream) {
        try {
            int length;
            while (inputStream.available() > 0 && (length = inputStream.read(buffer, 0, buffer.length)) != -1) {
                stringBuffer.append(new String(buffer, 0, length, StandardCharsets.UTF_8));
            }

            String string = stringBuffer.toString();
            if (!string.contains("\n") && !string.contains("\r")) {
                return;
            }

            for (String input : string.split("\r")) {
                for (String text : input.split("\n")) {
                    if (!text.trim().isEmpty()) {
                        this.cache(text);
                    }
                }
            }

            stringBuffer.setLength(0);
        } catch (final IOException exception) {
            stringBuffer.setLength(0);
        }
    }

    private void cache(@NotNull String text) {
        if (text.equals(LINE_SEPARATOR)) {
            return;
        }

        while (this.recentScreenLines.size() > 256) {
            this.recentScreenLines.poll();
        }

        this.recentScreenLines.offer(text);
        ExecutorAPI.getInstance().getEventManager().callEvent(new ProcessScreenEntryCachedEvent(this, text));
    }
}
