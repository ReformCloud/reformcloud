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

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.network.data.ProtocolBuffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DefaultRuntimeConfiguration implements RuntimeConfiguration {

  private int initialJvmMemoryAllocation;
  private int maximumJvmMemoryAllocation;
  private int dynamicMemory;
  private Collection<String> processParameters;
  private Collection<String> jvmOptions;
  private Map<String, String> systemProperties;
  private Collection<String> shutdownCommands;

  protected DefaultRuntimeConfiguration() {
  }

  protected DefaultRuntimeConfiguration(int initialJvmMemoryAllocation, int maximumJvmMemoryAllocation, int dynamicMemory, Collection<String> processParameters,
                                        Collection<String> jvmOptions, Map<String, String> systemProperties, Collection<String> shutdownCommands) {
    this.initialJvmMemoryAllocation = initialJvmMemoryAllocation;
    this.maximumJvmMemoryAllocation = maximumJvmMemoryAllocation;
    this.dynamicMemory = dynamicMemory;
    this.processParameters = processParameters;
    this.jvmOptions = jvmOptions;
    this.systemProperties = systemProperties;
    this.shutdownCommands = shutdownCommands;
  }

  @Override
  public int getInitialJvmMemoryAllocation() {
    return this.initialJvmMemoryAllocation;
  }

  @Override
  public void setInitialJvmMemoryAllocation(int initialJvmMemoryAllocation) {
    this.initialJvmMemoryAllocation = initialJvmMemoryAllocation;
  }

  @Override
  public int getMaximumJvmMemoryAllocation() {
    return this.maximumJvmMemoryAllocation;
  }

  @Override
  public void setMaximumJvmMemoryAllocation(int maximumJvmMemoryAllocation) {
    this.maximumJvmMemoryAllocation = maximumJvmMemoryAllocation;
  }

  @Override
  public int getDynamicMemory() {
    return this.dynamicMemory;
  }

  @Override
  public void setDynamicMemory(int dynamicMemory) {
    this.dynamicMemory = dynamicMemory;
  }

  @Override
  public @NotNull Collection<String> getProcessParameters() {
    return this.processParameters;
  }

  @Override
  public @NotNull Collection<String> getJvmOptions() {
    return this.jvmOptions;
  }

  @Override
  public @NotNull Map<String, String> getSystemProperties() {
    return this.systemProperties;
  }

  @Override
  public @NotNull Collection<String> getShutdownCommands() {
    return this.shutdownCommands;
  }

  @Override
  public @NotNull RuntimeConfiguration clone() {
    return new DefaultRuntimeConfiguration(
      this.initialJvmMemoryAllocation,
      this.maximumJvmMemoryAllocation,
      this.dynamicMemory,
      new ArrayList<>(this.processParameters),
      new ArrayList<>(this.jvmOptions),
      new HashMap<>(this.systemProperties),
      new ArrayList<>(this.shutdownCommands)
    );
  }

  @Override
  public void write(@NotNull ProtocolBuffer buffer) {
    buffer.writeInt(this.initialJvmMemoryAllocation);
    buffer.writeInt(this.maximumJvmMemoryAllocation);
    buffer.writeInt(this.dynamicMemory);
    buffer.writeStringArray(this.processParameters);
    buffer.writeStringArray(this.jvmOptions);
    buffer.writeStringMap(this.systemProperties);
    buffer.writeStringArray(this.shutdownCommands);
  }

  @Override
  public void read(@NotNull ProtocolBuffer buffer) {
    this.initialJvmMemoryAllocation = buffer.readInt();
    this.maximumJvmMemoryAllocation = buffer.readInt();
    this.dynamicMemory = buffer.readInt();
    this.processParameters = buffer.readStringArray();
    this.jvmOptions = buffer.readStringArray();
    this.systemProperties = buffer.readStringMap();
    this.shutdownCommands = buffer.readStringArray();
  }
}
