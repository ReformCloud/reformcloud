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
package systems.reformcloud.reformcloud2.executor.api;

import com.sun.management.OperatingSystemMXBean;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.utility.optional.ReferencedOptional;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.management.*;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

@ApiStatus.Internal
public final class CommonHelper {

    public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    public static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy kk:mm:ss");
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.###");
    private static final Map<Class<? extends Enum<?>>, Map<String, WeakReference<? extends Enum<?>>>> CACHE = new HashMap<>();

    private CommonHelper() {
        throw new UnsupportedOperationException();
    }

    public static MemoryMXBean memoryMXBean() {
        return ManagementFactory.getMemoryMXBean();
    }

    public static ClassLoadingMXBean classLoadingMXBean() {
        return ManagementFactory.getClassLoadingMXBean();
    }

    public static OperatingSystemMXBean operatingSystemMXBean() {
        return (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    public static RuntimeMXBean runtimeMXBean() {
        return ManagementFactory.getRuntimeMXBean();
    }

    public static ThreadMXBean threadMXBean() {
        return ManagementFactory.getThreadMXBean();
    }

    public static double cpuUsageSystem() {
        return operatingSystemMXBean().getSystemCpuLoad() * 100;
    }

    public static Collection<String> inputArguments() {
        return runtimeMXBean().getInputArguments();
    }

    public static long memoryPoolMXBeanCollectionUsage() {
        AtomicLong atomicLong = new AtomicLong(0);
        ManagementFactory.getMemoryPoolMXBeans().forEach(memoryPoolMXBean -> {
            if (memoryPoolMXBean.getCollectionUsage() == null) {
                return;
            }

            atomicLong.addAndGet(memoryPoolMXBean.getCollectionUsage().getUsed() / ProcessRuntimeInformation.MEGABYTE);
        });

        return atomicLong.get();
    }

    public static Integer fromString(String s) {
        try {
            return Integer.parseInt(s);
        } catch (final Throwable throwable) {
            return null;
        }
    }

    public static Boolean booleanFromString(String s) {
        if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false")) {
            return s.equalsIgnoreCase("true");
        }

        return null;
    }

    public static Long longFromString(String s) {
        try {
            return Long.parseLong(s);
        } catch (final Throwable throwable) {
            return null;
        }
    }

    public static UUID tryParse(String s) {
        try {
            return UUID.fromString(s);
        } catch (final Throwable throwable) {
            return null;
        }
    }

    public static int longToInt(long in) {
        return in > Integer.MAX_VALUE ? Integer.MAX_VALUE : in < Integer.MIN_VALUE ? Integer.MIN_VALUE : (int) in;
    }

    public static int calculateMaxMemory() {
        return (int) ((operatingSystemMXBean().getTotalPhysicalMemorySize() / 1048576) - 2048);
    }

    public static String getIpAddress(@NotNull String input) {
        if (input.split("\\.").length == 4) {
            return input;
        }

        try {
            return InetAddress.getByName(input).getHostAddress();
        } catch (final Throwable throwable) {
            return null;
        }
    }

    /* == Enum Helper == */

    public static void rewriteProperties(String path, String saveComment, Function<String, String> function) {
        if (!Files.exists(Paths.get(path))) {
            return;
        }

        try (InputStream inputStream = Files.newInputStream(Paths.get(path))) {
            Properties properties = new Properties();
            properties.load(inputStream);

            properties.stringPropertyNames().forEach(s -> properties.setProperty(s, function.apply(s)));

            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get(path)), StandardCharsets.UTF_8)) {
                properties.store(outputStreamWriter, saveComment);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    @NotNull
    public static Runnable newReportedRunnable(@NotNull Runnable source, @Nullable String reportedMessage) {
        return () -> {
            try {
                source.run();
            } catch (final Throwable throwable) {
                System.err.println("An internal exception while execution of runnable:");
                System.err.println(reportedMessage == null ? source.getClass().getName() : reportedMessage);
                System.err.println("Please report this either on github or discord including the full stack trace and above message");
                throwable.printStackTrace();
            }
        };
    }

    public static <T extends Enum<T>> ReferencedOptional<T> findEnumField(Class<T> enumClass, String field) {
        Map<String, WeakReference<? extends Enum<?>>> cached = CACHE.computeIfAbsent(enumClass, aClass -> cache(enumClass));

        WeakReference<? extends Enum<?>> reference = cached.get(field);
        return reference == null ? ReferencedOptional.empty() : ReferencedOptional.build(enumClass.cast(reference.get()));
    }

    private static <T extends Enum<T>> Map<String, WeakReference<? extends Enum<?>>> cache(Class<T> enumClass) {
        Map<String, WeakReference<? extends Enum<?>>> out = new HashMap<>();
        try {
            Collection<T> instance = EnumSet.allOf(enumClass);
            instance.forEach(t -> out.put(t.name(), new WeakReference<>(t)));
        } catch (final Throwable ignored) {
        }

        return out;
    }
}
