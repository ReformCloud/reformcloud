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
package systems.reformcloud.reformcloud2.shared.platform;

import com.sun.management.OperatingSystemMXBean;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.shared.Constants;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@ApiStatus.Internal
public final class Platform {

    private static final OperatingSystemMXBean OPERATING_SYSTEM_MX_BEAN = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    private Platform() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static long[] getDeadlockedThreads() {
        if (ManagementFactory.getThreadMXBean().isSynchronizerUsageSupported()) {
            long[] deadlockedThreads = ManagementFactory.getThreadMXBean().findDeadlockedThreads();
            return deadlockedThreads == null ? new long[0] : deadlockedThreads;
        } else {
            return new long[0];
        }
    }

    public static void closeProcess(@Nullable Process process, boolean await, long timeOut, @NonNls String... shutdownCommands) {
        if (process != null && process.isAlive()) {
            try {
                OutputStream outputStream = process.getOutputStream();
                for (String shutdownCommand : shutdownCommands) {
                    outputStream.write((shutdownCommand + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                }
            } catch (IOException ignored) {
            }

            try {
                if (process.waitFor(5, TimeUnit.SECONDS)) {
                    return;
                }
            } catch (InterruptedException exception) {
                process.destroy();
            }

            if (await) {
                boolean closed = false;
                long terminationMaxTime = System.currentTimeMillis() + timeOut;

                while (!closed) {
                    if (System.currentTimeMillis() >= terminationMaxTime) {
                        break;
                    } else {
                        try {
                            process.exitValue();
                            closed = true;
                        } catch (IllegalThreadStateException exception) {
                            process.destroy();
                        }
                    }
                }
            }
        }
    }

    public static long getMemoryPoolMXBeanCollectionUsage() {
        long result = 0;
        for (MemoryPoolMXBean memoryPoolMXBean : ManagementFactory.getMemoryPoolMXBeans()) {
            result += (memoryPoolMXBean.getCollectionUsage().getUsed() / Constants.MEGABYTE);
        }
        return result;
    }

    public static int getTotalSystemMemory() {
        return (int) (OPERATING_SYSTEM_MX_BEAN.getTotalPhysicalMemorySize() / Constants.MEGABYTE);
    }

    @NotNull
    public static OperatingSystemMXBean getOperatingSystemMxBean() {
        return OPERATING_SYSTEM_MX_BEAN;
    }
}
