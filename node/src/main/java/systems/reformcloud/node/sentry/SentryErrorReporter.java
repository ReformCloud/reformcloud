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
package systems.reformcloud.node.sentry;

import io.sentry.Sentry;
import io.sentry.SentryEvent;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.node.NodeExecutor;
import systems.reformcloud.shared.platform.Platform;

final class SentryErrorReporter {

  private static final String SENTRY_DSN = System.getProperty(
    "reformcloud.sentry-log-dsn",
    "https://f9ac673179f7438ab441e3bd61a32a90@o440889.ingest.sentry.io/5416465?async=false"
  );

  protected static void init(@NotNull NodeExecutor nodeExecutor) {
    Sentry.init(options -> {
      options.setDsn(SENTRY_DSN);
      options.addEventProcessor((event, hint) -> nodeExecutor.getNodeConfig().isSendAnonymousErrorReports() ? SentryErrorReporter.appendExtraInformation(event) : null);
    });
  }

  @NotNull
  private static SentryEvent appendExtraInformation(@NotNull SentryEvent event) {
    event.setRelease(System.getProperty("reformcloud.runner.version", "unsupported"));
    event.setTag("java_version", System.getProperty("java.vm.name") + " (" + System.getProperty("java.runtime.version") + ")");

    event.setExtra("system.process_memory_free", formatBytes(Runtime.getRuntime().freeMemory()));
    event.setExtra("system.process_memory_total", formatBytes(Runtime.getRuntime().totalMemory()));
    event.setExtra("system.user_name", System.getProperty("user.name"));
    event.setExtra("system.user_dir", System.getProperty("user.dir"));
    event.setExtra("system.os", System.getProperty("os.name") + " (Version: " + System.getProperty("os.version") + " Arch: " + System.getProperty("os.arch") + ")");
    event.setExtra("system.cpu", Runtime.getRuntime().availableProcessors());
    event.setExtra("system.memory", formatBytes(Platform.getTotalSystemMemory()));
    event.setExtra("system.current_thread", Thread.currentThread().getName());

    return event;
  }

  /* Thanks stackoverflow */
  @NotNull
  private static String formatBytes(long bytes) {
    if (bytes < 1024) {
      return bytes + " B";
    }

    int exp = (int) (Math.log(bytes) / Math.log(1024));
    char pre = "KMGTPE".charAt(exp - 1);
    return String.format("%.1f %siB", bytes / Math.pow(1024, exp), pre);
  }
}
