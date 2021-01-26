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
import io.netty.util.ResourceLeakDetector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessRuntimeInformation;
import systems.reformcloud.reformcloud2.shared.Constants;
import systems.reformcloud.reformcloud2.shared.parser.Parsers;
import systems.reformcloud.reformcloud2.shared.process.DefaultProcessRuntimeInformation;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@ApiStatus.Internal
public final class Platform {

  private static final OperatingSystemMXBean OPERATING_SYSTEM_MX_BEAN = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

  static {
    if (System.getProperty("io.netty.leakDetectionLevel") == null) {
      ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
    }

    System.getProperties().putIfAbsent("io.netty.allocator.maxOrder", "9");
    System.getProperties().putIfAbsent("io.netty.noPreferDirect", "true");
    System.getProperties().putIfAbsent("io.netty.maxDirectMemory", "0");
    System.getProperties().putIfAbsent("io.netty.recycler.maxCapacity", "0");
    System.getProperties().putIfAbsent("io.netty.recycler.maxCapacity.default", "0");
    System.getProperties().putIfAbsent("io.netty.selectorAutoRebuildThreshold", "0");
    System.getProperties().putIfAbsent("io.netty.allocator.type", "pooled");

    Thread.setDefaultUncaughtExceptionHandler((t, ex) -> ex.printStackTrace());
  }

  private Platform() {
    throw new UnsupportedOperationException();
  }

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

  @NotNull
  public static ProcessRuntimeInformation createProcessRuntimeInformation() {
    return new DefaultProcessRuntimeInformation(
      OPERATING_SYSTEM_MX_BEAN.getSystemCpuLoad() * 100,
      OPERATING_SYSTEM_MX_BEAN.getProcessCpuLoad() * 100,
      OPERATING_SYSTEM_MX_BEAN.getSystemLoadAverage() * 100,
      Runtime.getRuntime().availableProcessors(),
      Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(),
      ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / Constants.MEGABYTE,
      ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed() / Constants.MEGABYTE,
      getMemoryPoolMXBeanCollectionUsage(),
      ManagementFactory.getClassLoadingMXBean().getLoadedClassCount(),
      ManagementFactory.getClassLoadingMXBean().getUnloadedClassCount(),
      ManagementFactory.getClassLoadingMXBean().getTotalLoadedClassCount(),
      System.getProperty("os.name"),
      System.getProperty("java.version"),
      System.getProperty("os.arch"),
      ManagementFactory.getRuntimeMXBean().getInputArguments().toArray(new String[0]),
      Thread.getAllStackTraces().size(),
      getDeadlockedThreads(),
      ManagementFactory.getRuntimeMXBean().getSystemProperties(),
      ManagementFactory.getRuntimeMXBean().getClassPath(),
      ManagementFactory.getRuntimeMXBean().isBootClassPathSupported() ? ManagementFactory.getRuntimeMXBean().getBootClassPath() : "unsupported",
      currentPid()
    );
  }

  private static long getMemoryPoolMXBeanCollectionUsage() {
    long result = 0;
    for (MemoryPoolMXBean memoryPoolMXBean : ManagementFactory.getMemoryPoolMXBeans()) {
      final MemoryUsage usage = memoryPoolMXBean.getCollectionUsage();
      if (usage != null) {
        result += (usage.getUsed() / Constants.MEGABYTE);
      }
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

  private static long currentPid() {
    final String runtimeName = ManagementFactory.getRuntimeMXBean().getName();
    final int index = runtimeName.indexOf('@');
    if (index != -1) {
      final Long pid = Parsers.LONG.parse(runtimeName.substring(0, index));
      return pid == null ? 0L : pid;
    }
    return 0L;
  }
}
