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
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.event.EventManager;
import systems.reformcloud.node.concurrent.AsyncCatcher;
import systems.reformcloud.node.event.scheduler.SchedulerFullHeartBeatPermanentTaskExecuteEvent;
import systems.reformcloud.node.event.scheduler.SchedulerHeartBeatTaskExecuteEvent;
import systems.reformcloud.task.Task;
import systems.reformcloud.task.defaults.DefaultTask;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public final class TickedTaskScheduler {

  private final Queue<TickedTaskSchedulerTask<?>> queue = new ConcurrentLinkedQueue<>();
  private final Collection<Runnable> permanentTasks = new CopyOnWriteArrayList<>();

  private boolean closed = false;

  @NotNull
  public <T> Task<T> queue(@NotNull Callable<T> callable) {
    Task<T> task = new DefaultTask<>();
    this.queue.add(new TickedTaskSchedulerTask<>(task, callable, -1));
    return task;
  }

  @NotNull
  public <T> Task<T> queue(@NotNull Callable<T> callable, int delay) {
    Task<T> task = new DefaultTask<>();
    this.queue.add(new TickedTaskSchedulerTask<>(task, callable, CloudTickWorker.CURRENT_TICK.get() + delay));
    return task;
  }

  @NotNull
  public Task<Void> queue(@NotNull Runnable runnable) {
    return this.queue(() -> {
      runnable.run();
      return null;
    });
  }

  @NotNull
  public Task<Void> queue(@NotNull Runnable runnable, int delay) {
    return this.queue(() -> {
      runnable.run();
      return null;
    }, delay);
  }

  public void addPermanentTask(@NotNull Runnable runnable) {
    this.permanentTasks.add(runnable);
  }

  public void close() {
    synchronized (this) {
      if (this.closed) {
        return;
      }

      this.closed = true;
    }
  }

  void heartBeat() {
    if (this.closed) {
      return;
    }

    AsyncCatcher.ensureMainThread("scheduler heart beat");

    TickedTaskSchedulerTask<?> next = this.element();
    if (next != null) {
      try {
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new SchedulerHeartBeatTaskExecuteEvent(next));
        next.call();
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }
  }

  void fullHeartBeat() {
    if (this.closed) {
      return;
    }

    AsyncCatcher.ensureMainThread("scheduler full heart beat");

    for (Runnable permanentTask : this.permanentTasks) {
      try {
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).callEvent(new SchedulerFullHeartBeatPermanentTaskExecuteEvent(permanentTask));
        permanentTask.run();
      } catch (final Throwable throwable) {
        throwable.printStackTrace();
      }
    }
  }

  private @Nullable TickedTaskSchedulerTask<?> element() {
    for (TickedTaskSchedulerTask<?> tickedTaskSchedulerTask : this.queue) {
      if (tickedTaskSchedulerTask.getTargetTick() < 0 || tickedTaskSchedulerTask.getTargetTick() == CloudTickWorker.CURRENT_TICK.get()) {
        this.queue.remove(tickedTaskSchedulerTask);
        return tickedTaskSchedulerTask;
      }
    }

    return null;
  }

  public static class TickedTaskSchedulerTask<T> {

    private final Task<T> task;
    private final Callable<T> callable;
    private final long targetTick;

    public TickedTaskSchedulerTask(Task<T> task, Callable<T> callable, long targetTick) {
      this.task = task;
      this.callable = callable;
      this.targetTick = targetTick;
    }

    public long getTargetTick() {
      return this.targetTick;
    }

    public void call() {
      try {
        this.task.complete(this.callable.call());
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }
  }
}
