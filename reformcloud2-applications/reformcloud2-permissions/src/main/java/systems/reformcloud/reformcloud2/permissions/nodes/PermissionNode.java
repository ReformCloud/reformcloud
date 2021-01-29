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
package systems.reformcloud.reformcloud2.permissions.nodes;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.network.data.SerializableObject;

public class PermissionNode implements SerializableObject {

  private long addTime;
  private long timeout;
  private boolean set;
  private String actualPermission;

  public PermissionNode() {
  }

  public PermissionNode(long addTime, long timeout, boolean set, @NotNull String actualPermission) {
    this.addTime = addTime;
    this.timeout = timeout;
    this.set = set;
    this.actualPermission = actualPermission;
  }

  @NotNull
  public static PermissionNode createNode(@NotNull String permission, long timeout, boolean positive) {
    return new PermissionNode(System.currentTimeMillis(), timeout, positive, permission);
  }

  public long getAddTime() {
    return this.addTime;
  }

  public long getTimeout() {
    return this.timeout;
  }

  public boolean isSet() {
    return this.set && (this.timeout == -1 || this.timeout > System.currentTimeMillis());
  }

  public boolean isValid() {
    return this.timeout == -1 || this.timeout > System.currentTimeMillis();
  }

  @NotNull
  public String getActualPermission() {
    return this.actualPermission;
  }

  @Override
  public void write(@NotNull ProtocolBuffer buffer) {
    buffer.writeLong(this.addTime);
    buffer.writeLong(this.timeout);
    buffer.writeBoolean(this.set);
    buffer.writeString(this.actualPermission);
  }

  @Override
  public void read(@NotNull ProtocolBuffer buffer) {
    this.addTime = buffer.readLong();
    this.timeout = buffer.readLong();
    this.set = buffer.readBoolean();
    this.actualPermission = buffer.readString();
  }
}
