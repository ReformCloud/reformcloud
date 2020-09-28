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
package systems.reformcloud.reformcloud2.node.sentry;

import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.event.EventBuilder;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.CommonHelper;
import systems.reformcloud.reformcloud2.node.NodeExecutor;

/* package */ final class SentryErrorReporter {

    private static final String SENTRY_DSN = System.getProperty(
        "systems.reformcloud.sentry-log-dsn",
        "https://f9ac673179f7438ab441e3bd61a32a90@o440889.ingest.sentry.io/5416465?async=false"
    );

    protected static void init(NodeExecutor nodeExecutor) {
        SentryClient sentryClient = Sentry.init(SENTRY_DSN);
        sentryClient.addShouldSendEventCallback(ignored -> nodeExecutor.getNodeConfig().isSendAnonymousErrorReports());
        sentryClient.addBuilderHelper(SentryErrorReporter::appendExtraInformation);
    }

    private static void appendExtraInformation(@NotNull EventBuilder eventBuilder) {
        eventBuilder
            .withRelease(System.getProperty("reformcloud.runner.version", "unsupported"))

            .withTag("java_version", System.getProperty("java.vm.name") + " (" + System.getProperty("java.runtime.version") + ")")

            .withExtra("system.process_memory_free", formatBytes(Runtime.getRuntime().freeMemory()))
            .withExtra("system.process_memory_total", formatBytes(Runtime.getRuntime().totalMemory()))

            .withExtra("system.user_name", System.getProperty("user.name"))
            .withExtra("system.user_dir", System.getProperty("user.dir"))
            .withExtra("system.os", System.getProperty("os.name") + " (Version: " + System.getProperty("os.version") + " Arch: " + System.getProperty("os.arch") + ")")
            .withExtra("system.cpu", Runtime.getRuntime().availableProcessors())
            .withExtra("system.memory", formatBytes(CommonHelper.getTotalSystemMemory()))

            .withExtra("system.current_thread", Thread.currentThread().getName());
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
