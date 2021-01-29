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

public interface RuntimeConfiguration extends SerializableObject, Cloneable {

  @NotNull
  @Contract(value = "_, _, _ -> new", pure = true)
  static RuntimeConfiguration configuration(int initialJvmMemoryAllocation, int maximumJvmMemoryAllocation, int dynamicMemory) {
    return configuration(initialJvmMemoryAllocation, maximumJvmMemoryAllocation, dynamicMemory, new ArrayList<>());
  }

  @NotNull
  @Contract(value = "_, _, _, _ -> new", pure = true)
  static RuntimeConfiguration configuration(int initialJvmMemoryAllocation, int maximumJvmMemoryAllocation, int dynamicMemory,
                                            @NotNull Collection<String> processParameters
  ) {
    return configuration(initialJvmMemoryAllocation, maximumJvmMemoryAllocation, dynamicMemory, processParameters, new ArrayList<>());
  }

  @NotNull
  @Contract(value = "_, _, _, _, _ -> new", pure = true)
  static RuntimeConfiguration configuration(int initialJvmMemoryAllocation, int maximumJvmMemoryAllocation, int dynamicMemory,
                                            @NotNull Collection<String> processParameters, @NotNull Collection<String> jvmOptions
  ) {
    return configuration(initialJvmMemoryAllocation, maximumJvmMemoryAllocation, dynamicMemory, processParameters, jvmOptions, new HashMap<>());
  }

  @NotNull
  @Contract(value = "_, _, _, _, _, _ -> new", pure = true)
  static RuntimeConfiguration configuration(int initialJvmMemoryAllocation, int maximumJvmMemoryAllocation, int dynamicMemory,
                                            @NotNull Collection<String> processParameters, @NotNull Collection<String> jvmOptions,
                                            @NotNull Map<String, String> systemProperties
  ) {
    return configuration(initialJvmMemoryAllocation, maximumJvmMemoryAllocation, dynamicMemory, processParameters, jvmOptions, systemProperties, new ArrayList<>());
  }

  @NotNull
  @Contract(value = "_, _, _, _, _, _, _ -> new", pure = true)
  static RuntimeConfiguration configuration(int initialJvmMemoryAllocation, int maximumJvmMemoryAllocation, int dynamicMemory,
                                            @NotNull Collection<String> processParameters, @NotNull Collection<String> jvmOptions,
                                            @NotNull Map<String, String> systemProperties, @NotNull Collection<String> shutdownCommands
  ) {
    return new DefaultRuntimeConfiguration(initialJvmMemoryAllocation, maximumJvmMemoryAllocation, dynamicMemory,
      processParameters, jvmOptions, systemProperties, shutdownCommands);
  }

  int getInitialJvmMemoryAllocation();

  void setInitialJvmMemoryAllocation(int initialJvmMemoryAllocation);

  int getMaximumJvmMemoryAllocation();

  void setMaximumJvmMemoryAllocation(int maximumJvmMemoryAllocation);

  int getDynamicMemory();

  void setDynamicMemory(int dynamicMemory);

  @NotNull
  Collection<String> getProcessParameters();

  @NotNull
  Collection<String> getJvmOptions();

  @NotNull
  Map<String, String> getSystemProperties();

  @NotNull
  Collection<String> getShutdownCommands();

  @NotNull
  RuntimeConfiguration clone();
}
