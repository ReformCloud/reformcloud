package de.klaro.reformcloud2.executor.api.common;

import com.sun.management.OperatingSystemMXBean;
import de.klaro.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.management.*;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public final class CommonHelper {

    private CommonHelper() {}

    public static final Executor EXECUTOR = Executors.newCachedThreadPool();

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

    public static long memoryPoolMXBeanCollectionUsage() {
        AtomicLong atomicLong = new AtomicLong(0);
        ManagementFactory.getMemoryPoolMXBeans().forEach(memoryPoolMXBean -> {
            if (memoryPoolMXBean.getCollectionUsage() == null) {
                return;
            }

            atomicLong.addAndGet(memoryPoolMXBean.getCollectionUsage().getUsed());
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

    public static UUID tryParse(String s) {
        try {
            return UUID.fromString(s);
        } catch (final Throwable throwable) {
            return null;
        }
    }

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

    public static int calculateMaxMemory() {
        return (int) ((operatingSystemMXBean().getTotalPhysicalMemorySize() / 1048576) - 2048);
    }

    /* == Enum Helper == */

    private static final Map<Class<? extends Enum<?>>, Map<String, WeakReference<? extends Enum<?>>>> CACHE = new HashMap<>();

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
