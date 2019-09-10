package de.klaro.reformcloud2.executor.api.common;

import com.sun.management.OperatingSystemMXBean;
import de.klaro.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;

import java.lang.management.*;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
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
        ManagementFactory.getMemoryPoolMXBeans().forEach(new Consumer<MemoryPoolMXBean>() {
            @Override
            public void accept(MemoryPoolMXBean memoryPoolMXBean) {
                if (memoryPoolMXBean.getCollectionUsage() == null) {
                    return;
                }

                atomicLong.addAndGet(memoryPoolMXBean.getCollectionUsage().getUsed());
            }
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

    /* == Enum Helper == */

    private static final Map<Class<? extends Enum<?>>, Map<String, WeakReference<? extends Enum<?>>>> CACHE = new HashMap<>();

    public static <T extends Enum<T>> ReferencedOptional<T> findEnumField(Class<T> enumClass, String field) {
        Map<String, WeakReference<? extends Enum<?>>> cached = CACHE.computeIfAbsent(enumClass, new Function<Class<? extends Enum<?>>, Map<String, WeakReference<? extends Enum<?>>>>() {
            @Override
            public Map<String, WeakReference<? extends Enum<?>>> apply(Class<? extends Enum<?>> aClass) {
                return cache(enumClass);
            }
        });

        WeakReference<? extends Enum<?>> reference = cached.get(field);
        return reference == null ? ReferencedOptional.empty() : ReferencedOptional.build(enumClass.cast(reference.get()));
    }

    private static <T extends Enum<T>> Map<String, WeakReference<? extends Enum<?>>> cache(Class<T> enumClass) {
        Map<String, WeakReference<? extends Enum<?>>> out = new HashMap<>();
        try {
            Collection<T> instance = EnumSet.allOf(enumClass);
            instance.forEach(new Consumer<T>() {
                @Override
                public void accept(T t) {
                    out.put(t.name(), new WeakReference<>(t));
                }
            });
        } catch (final Throwable ignored) {
        }

        return out;
    }
}
