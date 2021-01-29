/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
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
package systems.reformcloud.node.tick;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.event.EventManager;
import systems.reformcloud.node.NodeExecutor;
import systems.reformcloud.node.event.worker.WorkerFullTickEvent;
import systems.reformcloud.node.event.worker.WorkerTickEvent;

public final class CloudTickWorker {

  static final int TPS = 20;
  static final long SEC_IN_NANO = 1_000_000_000;
  private static final long TICK_TIME = SEC_IN_NANO / TPS;
  private static final long MAX_CATCHUP_BUFFER = TICK_TIME * TPS * 60L;
  protected static long currentTick = 0;
  private final TickedTaskScheduler taskScheduler;
  private final Thread mainThread;

  public CloudTickWorker(@NotNull TickedTaskScheduler taskScheduler) {
    this.taskScheduler = taskScheduler;
    this.mainThread = Thread.currentThread();
  }

  public void startTick() {
    long start = System.nanoTime();
    long lastTick = start - TICK_TIME;
    long catchupTime = 0;
    long curTime;
    long wait;

    while (NodeExecutor.isRunning()) {
      try {
        curTime = System.nanoTime();
        wait = TICK_TIME - (curTime - lastTick);

        if (wait > 0) {
          if (catchupTime < 2E6) {
            wait += Math.abs(catchupTime);
          }

          if (wait < catchupTime) {
            catchupTime -= wait;
            wait = 0;
          } else if (catchupTime > 2E6) {
            wait -= catchupTime;
            catchupTime -= catchupTime;
          }
        }

        if (wait > 0) {
          Thread.sleep(wait / 1_000_000);
          wait = TICK_TIME - (curTime - lastTick);
        }

        catchupTime = Math.min(MAX_CATCHUP_BUFFER, catchupTime - wait);
        if (++CloudTickWorker.currentTick % TPS == 0) {
          this.taskScheduler.fullHeartBeat();
          ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(WorkerFullTickEvent.INSTANCE);
        }

        lastTick = curTime;

        this.taskScheduler.heartBeat();
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(WorkerTickEvent.INSTANCE);
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }
  }

  @NotNull
  public Thread getMainThread() {
    return this.mainThread;
  }
}
