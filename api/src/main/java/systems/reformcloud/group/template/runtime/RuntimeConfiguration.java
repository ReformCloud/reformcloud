/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
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
package systems.reformcloud.group.template.runtime;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.network.data.SerializableObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A configuration for the runtime of a process.
 */
public interface RuntimeConfiguration extends SerializableObject, Cloneable {

  /**
   * Creates a new runtime configuration.
   *
   * @param initialJvmMemoryAllocation The initial memory allocation.
   * @param maximumJvmMemoryAllocation The maximum memory allocation.
   * @param dynamicMemory              The dynamic memory calculated against the amount of running processes.
   * @return The created runtime configuration.
   */
  @NotNull
  @Contract(value = "_, _, _ -> new", pure = true)
  static RuntimeConfiguration configuration(int initialJvmMemoryAllocation, int maximumJvmMemoryAllocation, int dynamicMemory) {
    return configuration(initialJvmMemoryAllocation, maximumJvmMemoryAllocation, dynamicMemory, new ArrayList<>());
  }

  /**
   * Creates a new runtime configuration.
   *
   * @param initialJvmMemoryAllocation The initial memory allocation.
   * @param maximumJvmMemoryAllocation The maximum memory allocation.
   * @param dynamicMemory              The dynamic memory calculated against the amount of running processes.
   * @param processParameters          The process parameters: {@code java -jar file.jar parameter1 parameter2}.
   * @return The created runtime configuration.
   */
  @NotNull
  @Contract(value = "_, _, _, _ -> new", pure = true)
  static RuntimeConfiguration configuration(int initialJvmMemoryAllocation, int maximumJvmMemoryAllocation, int dynamicMemory,
                                            @NotNull Collection<String> processParameters
  ) {
    return configuration(initialJvmMemoryAllocation, maximumJvmMemoryAllocation, dynamicMemory, processParameters, new ArrayList<>());
  }

  /**
   * Creates a new runtime configuration.
   *
   * @param initialJvmMemoryAllocation The initial memory allocation.
   * @param maximumJvmMemoryAllocation The maximum memory allocation.
   * @param dynamicMemory              The dynamic memory calculated against the amount of running processes.
   * @param processParameters          The process parameters: {@code java -jar file.jar parameter1 parameter2}.
   * @param jvmOptions                 The jvm options: {@code java parameter1 parameter2 -jar file.jar}.
   * @return The created runtime configuration.
   */
  @NotNull
  @Contract(value = "_, _, _, _, _ -> new", pure = true)
  static RuntimeConfiguration configuration(int initialJvmMemoryAllocation, int maximumJvmMemoryAllocation, int dynamicMemory,
                                            @NotNull Collection<String> processParameters, @NotNull Collection<String> jvmOptions
  ) {
    return configuration(initialJvmMemoryAllocation, maximumJvmMemoryAllocation, dynamicMemory, processParameters, jvmOptions, new HashMap<>());
  }

  /**
   * Creates a new runtime configuration.
   *
   * @param initialJvmMemoryAllocation The initial memory allocation.
   * @param maximumJvmMemoryAllocation The maximum memory allocation.
   * @param dynamicMemory              The dynamic memory calculated against the amount of running processes.
   * @param processParameters          The process parameters: {@code java -jar file.jar parameter1 parameter2}.
   * @param jvmOptions                 The jvm options: {@code java parameter1 parameter2 -jar file.jar}.
   * @param systemProperties           The system properties: {@code java -Dkey1=value1 -Dkey2=value2 -jar file.jar}
   * @return The created runtime configuration.
   */
  @NotNull
  @Contract(value = "_, _, _, _, _, _ -> new", pure = true)
  static RuntimeConfiguration configuration(int initialJvmMemoryAllocation, int maximumJvmMemoryAllocation, int dynamicMemory,
                                            @NotNull Collection<String> processParameters, @NotNull Collection<String> jvmOptions,
                                            @NotNull Map<String, String> systemProperties
  ) {
    return configuration(initialJvmMemoryAllocation, maximumJvmMemoryAllocation, dynamicMemory, processParameters, jvmOptions, systemProperties, new ArrayList<>());
  }

  /**
   * Creates a new runtime configuration.
   *
   * @param initialJvmMemoryAllocation The initial memory allocation.
   * @param maximumJvmMemoryAllocation The maximum memory allocation.
   * @param dynamicMemory              The dynamic memory calculated against the amount of running processes.
   * @param processParameters          The process parameters: {@code java -jar file.jar parameter1 parameter2}.
   * @param jvmOptions                 The jvm options: {@code java parameter1 parameter2 -jar file.jar}.
   * @param systemProperties           The system properties: {@code java -Dkey1=value1 -Dkey2=value2 -jar file.jar}
   * @param shutdownCommands           The shutdown commands for the server. {@code stop} and {@code end} are always present.
   * @return The created runtime configuration.
   */
  @NotNull
  @Contract(value = "_, _, _, _, _, _, _ -> new", pure = true)
  static RuntimeConfiguration configuration(int initialJvmMemoryAllocation, int maximumJvmMemoryAllocation, int dynamicMemory,
                                            @NotNull Collection<String> processParameters, @NotNull Collection<String> jvmOptions,
                                            @NotNull Map<String, String> systemProperties, @NotNull Collection<String> shutdownCommands
  ) {
    return new DefaultRuntimeConfiguration(initialJvmMemoryAllocation, maximumJvmMemoryAllocation, dynamicMemory,
      processParameters, jvmOptions, systemProperties, shutdownCommands);
  }

  /**
   * Get the initial heap memory allocation a process takes.
   *
   * @return The initial heap memory allocation a process takes.
   */
  int getInitialJvmMemoryAllocation();

  /**
   * Sets the initial heap memory allocation a process takes.
   *
   * @param initialJvmMemoryAllocation The initial heap memory allocation a process takes.
   */
  void setInitialJvmMemoryAllocation(int initialJvmMemoryAllocation);

  /**
   * Get the maximum heap memory allocation a process takes.
   *
   * @return The maximum heap memory allocation a process takes.
   */
  int getMaximumJvmMemoryAllocation();

  /**
   * Sets the maximum heap memory allocation a process takes.
   *
   * @param maximumJvmMemoryAllocation The maximum heap memory allocation a process takes.
   */
  void setMaximumJvmMemoryAllocation(int maximumJvmMemoryAllocation);

  /**
   * Get the dynamic memory of a group.
   *
   * @return The dynamic memory of a group.
   */
  int getDynamicMemory();

  /**
   * Sets the dynamic memory of a group.
   *
   * @param dynamicMemory The dynamic memory of a group.
   */
  void setDynamicMemory(int dynamicMemory);

  /**
   * Get the process parameters.
   *
   * @return The process parameters.
   */
  @NotNull
  Collection<String> getProcessParameters();

  /**
   * Get the jvm options.
   *
   * @return The jvm options.
   */
  @NotNull
  Collection<String> getJvmOptions();

  /**
   * Gets the system properties.
   *
   * @return The system properties.
   */
  @NotNull
  Map<String, String> getSystemProperties();

  /**
   * Gets the shutdown commands.
   *
   * @return The shutdown commands.
   */
  @NotNull
  Collection<String> getShutdownCommands();

  /**
   * Creates a clone of this configuration.
   *
   * @return A clone of this configuration.
   */
  @NotNull
  RuntimeConfiguration clone();
}
