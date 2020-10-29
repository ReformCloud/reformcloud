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
package systems.reformcloud.reformcloud2.executor.api;

import com.sun.management.OperatingSystemMXBean;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import systems.reformcloud.reformcloud2.executor.api.enums.EnumUtil;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.utility.optional.ReferencedOptional;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

@ApiStatus.Internal
public final class CommonHelper {

    public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    public static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    public static final ArrayDeque<String> EMPTY_STRING_QUEUE = new ArrayDeque<>();
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy kk:mm:ss");
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.###");

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
        return ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    }

    public static RuntimeMXBean runtimeMXBean() {
        return ManagementFactory.getRuntimeMXBean();
    }

    public static ThreadMXBean threadMXBean() {
        return ManagementFactory.getThreadMXBean();
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
        return getTotalSystemMemory() - 0x800;
    }

    public static int getTotalSystemMemory() {
        return (int) (operatingSystemMXBean().getTotalPhysicalMemorySize() / 0x100000);
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

    public static @Unmodifiable @NotNull Collection<String> getAllAvailableIpAddresses() {
        Collection<String> result = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                Enumeration<InetAddress> inetAddresses = networkInterfaces.nextElement().getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    String address = inetAddresses.nextElement().getHostAddress();
                    int location = address.indexOf('%');
                    if (location != -1) {
                        // % is used if java can detect which internet adapter the address is from
                        // It can look like: 0:0:0:0:0:0:0:0%eth2
                        // We decently remove the information about the internet adapter
                        address = address.substring(0, location);
                    }

                    if (!result.contains(address)) {
                        result.add(address);
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        return result;
    }

    public static void setSystemPropertyIfUnset(@NotNull String property, @NotNull String value) {
        if (System.getProperty(property) != null) {
            return;
        }

        if (System.getSecurityManager() != null) {
            AccessController.doPrivileged((PrivilegedAction<String>) () -> System.setProperty(property, value));
        } else {
            System.setProperty(property, value);
        }
    }

    public static @NotNull String formatThrowable(@NotNull Throwable throwable) {
        StackTraceElement[] trace = throwable.getStackTrace();
        return throwable.getClass().getSimpleName() + " : " + throwable.getMessage()
            + ((trace.length > 0) ? " @ " + throwable.getStackTrace()[0].getClassName() + ":" + throwable.getStackTrace()[0].getLineNumber() : "");
    }

    /**
     * Tries to find an enum field by the given name, using a weak cache
     *
     * @param enumClass The class to find the constant in
     * @param field     The name of the field to find
     * @param <T>       The type of the enum class
     * @return An optional which is empty if no field by the given value was found or containing the field associated with the name
     * @deprecated Use {@link EnumUtil#findEnumFieldByName(Class, String)} instead
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.10.3")
    public static <T extends Enum<T>> ReferencedOptional<T> findEnumField(Class<T> enumClass, String field) {
        return ReferencedOptional.build(EnumUtil.findEnumFieldByName(enumClass, field).orElse(null));
    }
}
