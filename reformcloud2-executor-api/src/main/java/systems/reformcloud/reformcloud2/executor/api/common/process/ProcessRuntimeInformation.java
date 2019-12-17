package systems.reformcloud.reformcloud2.executor.api.common.process;

import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ProcessRuntimeInformation {

    private double cpuUsageSystem;

    private double cpuUsageInternal;

    private double loadAverageSystem;

    private int processorCount;

    private long memoryUsageSystem;

    private long memoryUsageInternal;

    private long nonHeapMemoryUsage;

    private long collectionMemoryUsage;

    private int loadedClasses;

    private long unloadedClasses;

    private long totalLoadedClasses;

    private String osVersion;

    private String javaVersion;

    private String systemArchitecture;

    private String[] startParameters;

    private int stacktraces;

    private long[] deadLockedThreads;

    private Map<String, String> systemProperties;

    private String classPath;

    private String bootClassPath;

    private List<ThreadInfo> threadInfos;

    private ProcessRuntimeInformation(double cpuUsageSystem, double cpuUsageInternal, double loadAverageSystem,
                                    int processorCount, long memoryUsageSystem, long memoryUsageInternal,
                                    long nonHeapMemoryUsage, long collectionMemoryUsage, int loadedClasses,
                                    long unloadedClasses, long totalLoadedClasses, String osVersion,
                                    String javaVersion, String systemArchitecture, String[] startParameters,
                                    int stacktraces, long[] deadLockedThreads, Map<String, String> systemProperties,
                                     String classPath, String bootClassPath, List<ThreadInfo> threadInfos) {
        this.cpuUsageSystem = cpuUsageSystem;
        this.cpuUsageInternal = cpuUsageInternal;
        this.loadAverageSystem = loadAverageSystem;
        this.processorCount = processorCount;
        this.memoryUsageSystem = memoryUsageSystem;
        this.memoryUsageInternal = memoryUsageInternal;
        this.nonHeapMemoryUsage = nonHeapMemoryUsage;
        this.collectionMemoryUsage = collectionMemoryUsage;
        this.loadedClasses = loadedClasses;
        this.unloadedClasses = unloadedClasses;
        this.totalLoadedClasses = totalLoadedClasses;
        this.osVersion = osVersion;
        this.javaVersion = javaVersion;
        this.systemArchitecture = systemArchitecture;
        this.startParameters = startParameters;
        this.stacktraces = stacktraces;
        this.deadLockedThreads = deadLockedThreads;
        this.systemProperties = systemProperties;
        this.classPath = classPath;
        this.bootClassPath = bootClassPath;
        this.threadInfos = threadInfos;
    }

    public double getCpuUsageSystem() {
        return cpuUsageSystem;
    }

    public double getCpuUsageInternal() {
        return cpuUsageInternal;
    }

    public double getLoadAverageSystem() {
        return loadAverageSystem;
    }

    public int getProcessorCount() {
        return processorCount;
    }

    public long getMemoryUsageSystem() {
        return memoryUsageSystem;
    }

    public long getMemoryUsageInternal() {
        return memoryUsageInternal;
    }

    public long getNonHeapMemoryUsage() {
        return nonHeapMemoryUsage;
    }

    public long getCollectionMemoryUsage() {
        return collectionMemoryUsage;
    }

    public int getLoadedClasses() {
        return loadedClasses;
    }

    public long getUnloadedClasses() {
        return unloadedClasses;
    }

    public long getTotalLoadedClasses() {
        return totalLoadedClasses;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public String getSystemArchitecture() {
        return systemArchitecture;
    }

    public String[] getStartParameters() {
        return startParameters;
    }

    public int getStacktraces() {
        return stacktraces;
    }

    public long[] getDeadLockedThreads() {
        return deadLockedThreads;
    }

    public Map<String, String> getSystemProperties() {
        return systemProperties;
    }

    public String getClassPath() {
        return classPath;
    }

    public String getBootClassPath() {
        return bootClassPath;
    }

    public List<ThreadInfo> getThreadInfos() {
        return threadInfos;
    }

    public static ProcessRuntimeInformation create() {
        return new ProcessRuntimeInformation(
                CommonHelper.operatingSystemMXBean().getSystemCpuLoad() * 100,
                CommonHelper.operatingSystemMXBean().getProcessCpuLoad() * 100,
                CommonHelper.operatingSystemMXBean().getSystemLoadAverage() * 100,
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(),
                CommonHelper.memoryMXBean().getHeapMemoryUsage().getUsed(),
                CommonHelper.memoryMXBean().getNonHeapMemoryUsage().getUsed(),
                CommonHelper.memoryPoolMXBeanCollectionUsage(),
                CommonHelper.classLoadingMXBean().getLoadedClassCount(),
                CommonHelper.classLoadingMXBean().getUnloadedClassCount(),
                CommonHelper.classLoadingMXBean().getTotalLoadedClassCount(),
                System.getProperty("os.name"),
                System.getProperty("java.version"),
                System.getProperty("os.arch"),
                CommonHelper.runtimeMXBean().getInputArguments().toArray(new String[0]),
                Thread.getAllStackTraces().size(),
                CommonHelper.threadMXBean().findDeadlockedThreads(),
                CommonHelper.runtimeMXBean().getSystemProperties(),
                CommonHelper.runtimeMXBean().getClassPath(),
                CommonHelper.runtimeMXBean().isBootClassPathSupported() ? CommonHelper.runtimeMXBean().getBootClassPath() : "unknown",
                Links.keyApply(Thread.getAllStackTraces(), ThreadInfo::create)
        );
    }

    public static ProcessRuntimeInformation empty() {
        return new ProcessRuntimeInformation(
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                null,
                null,
                null,
                null,
                -1,
                new long[0],
                new HashMap<>(),
                null,
                null,
                new ArrayList<>()
        );
    }
}
