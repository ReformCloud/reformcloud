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
package systems.reformcloud.reformcloud2.executor.node.process.watchdog;

import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;

import java.util.concurrent.TimeUnit;

public final class WatchdogThread extends AbsoluteThread {

    public WatchdogThread() {
        updatePriority(Thread.MIN_PRIORITY).enableDaemon().start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Streams.newList(LocalProcessManager.getNodeProcesses()).forEach(runningProcess -> {
                if (!runningProcess.isAlive()
                        && runningProcess.getStartupTime() != -1
                        && runningProcess.getStartupTime() + TimeUnit.SECONDS.toMillis(30) < System.currentTimeMillis()) {
                    runningProcess.shutdown();
                }
            });

            AbsoluteThread.sleep(TimeUnit.SECONDS, 1);
        }
    }
}
