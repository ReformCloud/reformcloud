package systems.reformcloud.reformcloud2.executor.api.common.process;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessRuntimeInformation implements SerializableObject {

    @ApiStatus.Internal
    public ProcessRuntimeInformation() {
    }

    public static final long MEGABYTE = 1024L * 1024L;

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
        long[] longs = CommonHelper.threadMXBean().findDeadlockedThreads();
        return new ProcessRuntimeInformation(
                CommonHelper.operatingSystemMXBean().getSystemCpuLoad() * 100,
                CommonHelper.operatingSystemMXBean().getProcessCpuLoad() * 100,
                CommonHelper.operatingSystemMXBean().getSystemLoadAverage() * 100,
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(),
                CommonHelper.memoryMXBean().getHeapMemoryUsage().getUsed() / MEGABYTE,
                CommonHelper.memoryMXBean().getNonHeapMemoryUsage().getUsed() / MEGABYTE,
                CommonHelper.memoryPoolMXBeanCollectionUsage(),
                CommonHelper.classLoadingMXBean().getLoadedClassCount(),
                CommonHelper.classLoadingMXBean().getUnloadedClassCount(),
                CommonHelper.classLoadingMXBean().getTotalLoadedClassCount(),
                System.getProperty("os.name"),
                System.getProperty("java.version"),
                System.getProperty("os.arch"),
                CommonHelper.runtimeMXBean().getInputArguments().toArray(new String[0]),
                Thread.getAllStackTraces().size(),
                longs == null ? new long[0] : longs,
                CommonHelper.runtimeMXBean().getSystemProperties(),
                CommonHelper.runtimeMXBean().getClassPath(),
                CommonHelper.runtimeMXBean().isBootClassPathSupported() ? CommonHelper.runtimeMXBean().getBootClassPath() : "unknown",
                Streams.keyApply(Thread.getAllStackTraces(), ThreadInfo::create)
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

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeDouble(this.cpuUsageSystem);
        buffer.writeDouble(this.cpuUsageInternal);
        buffer.writeDouble(this.loadAverageSystem);
        buffer.writeInt(this.processorCount);
        buffer.writeLong(this.memoryUsageSystem);
        buffer.writeLong(this.memoryUsageInternal);
        buffer.writeLong(this.nonHeapMemoryUsage);
        buffer.writeLong(this.collectionMemoryUsage);
        buffer.writeInt(this.loadedClasses);
        buffer.writeLong(this.unloadedClasses);
        buffer.writeLong(this.totalLoadedClasses);
        buffer.writeString(this.osVersion);
        buffer.writeString(this.javaVersion);
        buffer.writeString(this.systemArchitecture);
        buffer.writeStringArrays(this.startParameters);
        buffer.writeInt(this.stacktraces);
        buffer.writeLongArray(this.deadLockedThreads);
        buffer.writeStringMap(this.systemProperties);
        buffer.writeString(this.classPath);
        buffer.writeString(this.bootClassPath);
        buffer.writeObjects(this.threadInfos);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.cpuUsageSystem = buffer.readDouble();
        this.cpuUsageInternal = buffer.readDouble();
        this.loadAverageSystem = buffer.readDouble();
        this.processorCount = buffer.readInt();
        this.memoryUsageSystem = buffer.readLong();
        this.memoryUsageInternal = buffer.readLong();
        this.nonHeapMemoryUsage = buffer.readLong();
        this.collectionMemoryUsage = buffer.readLong();
        this.loadedClasses = buffer.readInt();
        this.unloadedClasses = buffer.readLong();
        this.totalLoadedClasses = buffer.readLong();
        this.osVersion = buffer.readString();
        this.javaVersion = buffer.readString();
        this.systemArchitecture = buffer.readString();
        this.startParameters = buffer.readStringArrays();
        this.stacktraces = buffer.readInt();
        this.deadLockedThreads = buffer.readLongArray();
        this.systemProperties = buffer.readStringMap();
        this.classPath = buffer.readString();
        this.bootClassPath = buffer.readString();
        this.threadInfos = buffer.readObjects(ThreadInfo.class);
    }
}
