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
package systems.reformcloud.reformcloud2.shared.process;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessRuntimeInformation;

import java.util.HashMap;
import java.util.Map;

public class DefaultProcessRuntimeInformation implements ProcessRuntimeInformation {

  public static final ProcessRuntimeInformation EMPTY = new DefaultProcessRuntimeInformation(true);

  private long creationMillis;
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
  private int stacktrace;
  private long[] deadLockedThreads;
  private Map<String, String> systemProperties;
  private String classPath;
  private String bootClassPath;
  private long processId;

  DefaultProcessRuntimeInformation() {
  }

  private DefaultProcessRuntimeInformation(boolean ignored) {
    this.osVersion = "unknown";
    this.javaVersion = "unknown";
    this.systemArchitecture = "unknown";
    this.startParameters = new String[0];
    this.systemProperties = new HashMap<>();
    this.classPath = "unknown";
    this.bootClassPath = "unknown";
  }

  public DefaultProcessRuntimeInformation(double cpuUsageSystem, double cpuUsageInternal, double loadAverageSystem, int processorCount, long memoryUsageSystem,
                                          long memoryUsageInternal, long nonHeapMemoryUsage, long collectionMemoryUsage, int loadedClasses, long unloadedClasses,
                                          long totalLoadedClasses, String osVersion, String javaVersion, String systemArchitecture, String[] startParameters,
                                          int stacktrace, long[] deadLockedThreads, Map<String, String> systemProperties, String classPath, String bootClassPath, long processId) {
    this.creationMillis = System.currentTimeMillis();
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
    this.stacktrace = stacktrace;
    this.deadLockedThreads = deadLockedThreads;
    this.systemProperties = systemProperties;
    this.classPath = classPath;
    this.bootClassPath = bootClassPath;
    this.processId = processId;
  }

  public DefaultProcessRuntimeInformation(long creationMillis, double cpuUsageSystem, double cpuUsageInternal, double loadAverageSystem, int processorCount, long memoryUsageSystem,
                                          long memoryUsageInternal, long nonHeapMemoryUsage, long collectionMemoryUsage, int loadedClasses, long unloadedClasses,
                                          long totalLoadedClasses, String osVersion, String javaVersion, String systemArchitecture, String[] startParameters,
                                          int stacktrace, long[] deadLockedThreads, Map<String, String> systemProperties, String classPath, String bootClassPath, long processId) {
    this.creationMillis = creationMillis;
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
    this.stacktrace = stacktrace;
    this.deadLockedThreads = deadLockedThreads;
    this.systemProperties = systemProperties;
    this.classPath = classPath;
    this.bootClassPath = bootClassPath;
    this.processId = processId;
  }

  @Override
  public long getCreationMillis() {
    return this.creationMillis;
  }

  @Override
  public double getCpuUsageSystem() {
    return this.cpuUsageSystem;
  }

  @Override
  public double getCpuUsageInternal() {
    return this.cpuUsageInternal;
  }

  @Override
  public double getLoadAverageSystem() {
    return this.loadAverageSystem;
  }

  @Override
  public int getProcessorCount() {
    return this.processorCount;
  }

  @Override
  public long getMemoryUsageSystem() {
    return this.memoryUsageSystem;
  }

  @Override
  public long getMemoryUsageInternal() {
    return this.memoryUsageInternal;
  }

  @Override
  public long getNonHeapMemoryUsage() {
    return this.nonHeapMemoryUsage;
  }

  @Override
  public long getCollectionMemoryUsage() {
    return this.collectionMemoryUsage;
  }

  @Override
  public int getLoadedClasses() {
    return this.loadedClasses;
  }

  @Override
  public long getUnloadedClasses() {
    return this.unloadedClasses;
  }

  @Override
  public long getTotalLoadedClasses() {
    return this.totalLoadedClasses;
  }

  @Override
  public @NotNull String getOsVersion() {
    return this.osVersion;
  }

  @Override
  public @NotNull String getJavaVersion() {
    return this.javaVersion;
  }

  @Override
  public @NotNull String getSystemArchitecture() {
    return this.systemArchitecture;
  }

  @Override
  public @NotNull String[] getStartParameters() {
    return this.startParameters;
  }

  @Override
  public int getStacktraces() {
    return this.stacktrace;
  }

  @Override
  public long[] getDeadLockedThreads() {
    return this.deadLockedThreads;
  }

  @Override
  public @NotNull Map<String, String> getSystemProperties() {
    return this.systemProperties;
  }

  @Override
  public @NotNull String getClassPath() {
    return this.classPath;
  }

  @Override
  public @NotNull String getBootClassPath() {
    return this.bootClassPath;
  }

  @Override
  public long getProcessId() {
    return this.processId;
  }

  @Override
  public @NotNull ProcessRuntimeInformation clone() {
    return new DefaultProcessRuntimeInformation(
      this.creationMillis,
      this.cpuUsageSystem,
      this.cpuUsageInternal,
      this.loadAverageSystem,
      this.processorCount,
      this.memoryUsageSystem,
      this.memoryUsageInternal,
      this.nonHeapMemoryUsage,
      this.collectionMemoryUsage,
      this.loadedClasses,
      this.unloadedClasses,
      this.totalLoadedClasses,
      this.osVersion,
      this.javaVersion,
      this.systemArchitecture,
      this.startParameters,
      this.stacktrace,
      this.deadLockedThreads,
      new HashMap<>(this.systemProperties),
      this.classPath,
      this.bootClassPath,
      this.processId
    );
  }

  @Override
  public void write(@NotNull ProtocolBuffer buffer) {
    buffer.writeLong(this.creationMillis);
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
    buffer.writeInt(this.stacktrace);
    buffer.writeLongArray(this.deadLockedThreads);
    buffer.writeStringMap(this.systemProperties);
    buffer.writeString(this.classPath);
    buffer.writeString(this.bootClassPath);
    buffer.writeLong(this.processId);
  }

  @Override
  public void read(@NotNull ProtocolBuffer buffer) {
    this.creationMillis = buffer.readLong();
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
    this.stacktrace = buffer.readInt();
    this.deadLockedThreads = buffer.readLongArray();
    this.systemProperties = buffer.readStringMap();
    this.classPath = buffer.readString();
    this.bootClassPath = buffer.readString();
    this.processId = buffer.readLong();
  }
}
