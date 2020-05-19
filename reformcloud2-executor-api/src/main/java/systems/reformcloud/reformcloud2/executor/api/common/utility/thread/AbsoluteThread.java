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
package systems.reformcloud.reformcloud2.executor.api.common.utility.thread;

import java.util.concurrent.TimeUnit;

public abstract class AbsoluteThread extends Thread {

    private volatile boolean interrupted = false;

    public static void sleep(TimeUnit timeUnit, long time) {
        try {
            timeUnit.sleep(time);
        } catch (final InterruptedException ignored) {
        }
    }

    public static void sleep(long time) {
        sleep(TimeUnit.MILLISECONDS, time);
    }

    public AbsoluteThread enableDaemon() {
        this.setDaemon(true);
        return this;
    }

    public AbsoluteThread updatePriority(int priority) {
        this.setPriority(priority);
        return this;
    }

    public AbsoluteThread setExceptionHandler(UncaughtExceptionHandler handler) {
        this.setUncaughtExceptionHandler(handler);
        return this;
    }

    public AbsoluteThread updateClassLoader(ClassLoader classLoader) {
        this.setContextClassLoader(classLoader);
        return this;
    }

    @Override
    public void interrupt() {
        this.interrupted = true;
        super.interrupt();
    }

    @Override
    public boolean isInterrupted() {
        return this.interrupted;
    }

    @Override
    public abstract void run();
}
