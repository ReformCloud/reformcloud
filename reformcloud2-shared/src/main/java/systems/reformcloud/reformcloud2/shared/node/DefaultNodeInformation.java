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
package systems.reformcloud.reformcloud2.shared.node;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.network.address.NetworkAddress;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessRuntimeInformation;
import systems.reformcloud.reformcloud2.shared.platform.Platform;
import systems.reformcloud.reformcloud2.shared.process.DefaultProcessRuntimeInformation;

import java.lang.reflect.Type;
import java.util.UUID;

public class DefaultNodeInformation implements NodeInformation {

  public static final Type TYPE = new TypeToken<DefaultNodeInformation>() {
  }.getType();

  private String name;
  private UUID nodeUniqueID;

  private long startupTime;
  private long lastUpdate;
  private long usedMemory;
  private long maxMemory;

  private NetworkAddress processStartHost;
  private ProcessRuntimeInformation processRuntimeInformation;

  public DefaultNodeInformation() {
  }

  public DefaultNodeInformation(String name, UUID nodeUniqueID, long startupTime, long usedMemory, long maxMemory, NetworkAddress processStartHost) {
    this.name = name;
    this.nodeUniqueID = nodeUniqueID;
    this.startupTime = this.lastUpdate = startupTime;
    this.usedMemory = usedMemory;
    this.maxMemory = maxMemory;
    this.processRuntimeInformation = Platform.createProcessRuntimeInformation();
    this.processStartHost = processStartHost;
  }

  @NotNull
  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public @NotNull UUID getUniqueId() {
    return this.nodeUniqueID;
  }

  @Override
  public long getStartupMillis() {
    return this.startupTime;
  }

  @Override
  public long getLastUpdateTimestamp() {
    return this.lastUpdate;
  }

  @Override
  public long getUsedMemory() {
    return this.usedMemory;
  }

  @Override
  public long getMaxMemory() {
    return this.maxMemory;
  }

  @Override
  public @NotNull NetworkAddress getProcessStartHost() {
    return this.processStartHost;
  }

  @NotNull
  @Override
  public ProcessRuntimeInformation getProcessRuntimeInformation() {
    return this.processRuntimeInformation;
  }

  public void addUsedMemory(int memory) {
    this.usedMemory += memory;
  }

  public void removeUsedMemory(int memory) {
    this.usedMemory -= memory;
  }

  public void update() {
    this.processRuntimeInformation = Platform.createProcessRuntimeInformation();
    this.lastUpdate = System.currentTimeMillis();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof DefaultNodeInformation)) {
      return false;
    }

    DefaultNodeInformation that = (DefaultNodeInformation) o;
    return this.nodeUniqueID.equals(that.nodeUniqueID);
  }

  @Override
  public void write(@NotNull ProtocolBuffer buffer) {
    buffer.writeString(this.name);
    buffer.writeUniqueId(this.nodeUniqueID);
    buffer.writeLong(this.startupTime);
    buffer.writeLong(this.lastUpdate);
    buffer.writeLong(this.usedMemory);
    buffer.writeLong(this.maxMemory);
    buffer.writeObject(this.processRuntimeInformation);
  }

  @Override
  public void read(@NotNull ProtocolBuffer buffer) {
    this.name = buffer.readString();
    this.nodeUniqueID = buffer.readUniqueId();
    this.startupTime = buffer.readLong();
    this.lastUpdate = buffer.readLong();
    this.usedMemory = buffer.readLong();
    this.maxMemory = buffer.readLong();
    this.processRuntimeInformation = buffer.readObject(DefaultProcessRuntimeInformation.class, ProcessRuntimeInformation.class);
  }
}
