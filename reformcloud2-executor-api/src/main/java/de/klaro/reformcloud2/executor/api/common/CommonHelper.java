package de.klaro.reformcloud2.executor.api.common;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public final class CommonHelper {

    private CommonHelper() {}

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

    public static long memoryPoolMXBeanCollectionUsage() {
        AtomicLong atomicLong = new AtomicLong(0);
        ManagementFactory.getMemoryPoolMXBeans().forEach(new Consumer<MemoryPoolMXBean>() {
            @Override
            public void accept(MemoryPoolMXBean memoryPoolMXBean) {
                atomicLong.addAndGet(memoryPoolMXBean.getCollectionUsage().getUsed());
            }
        });

        return atomicLong.get();
    }
}
