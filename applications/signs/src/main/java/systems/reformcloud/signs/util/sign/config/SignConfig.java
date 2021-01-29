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
package systems.reformcloud.signs.util.sign.config;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.network.data.ProtocolBuffer;
import systems.reformcloud.network.data.SerializableObject;

import java.util.Collection;

public class SignConfig implements SerializableObject, Cloneable {

  private double updateIntervalInSeconds;
  private Collection<SignLayout> layouts;
  private boolean knockBackEnabled;
  private String knockBackBypassPermission;
  private double knockBackDistance;
  private double knockBackStrength;

  public SignConfig() {
  }

  public SignConfig(long updateIntervalInSeconds, Collection<SignLayout> layouts, boolean knockBackEnabled, String knockBackBypassPermission, double knockBackDistance, double knockBackStrength) {
    this.updateIntervalInSeconds = updateIntervalInSeconds;
    this.layouts = layouts;
    this.knockBackEnabled = knockBackEnabled;
    this.knockBackBypassPermission = knockBackBypassPermission;
    this.knockBackDistance = knockBackDistance;
    this.knockBackStrength = knockBackStrength;
  }

  public Collection<SignLayout> getLayouts() {
    return this.layouts;
  }

  public double getUpdateInterval() {
    return this.updateIntervalInSeconds >= 0 ? this.updateIntervalInSeconds : 1;
  }

  public boolean isKnockBackEnabled() {
    return this.knockBackEnabled;
  }

  public String getKnockBackBypassPermission() {
    return this.knockBackBypassPermission == null ? "reformcloud.knockback.bypass" : this.knockBackBypassPermission;
  }

  public double getKnockBackDistance() {
    return this.knockBackDistance;
  }

  public double getKnockBackStrength() {
    return this.knockBackStrength;
  }

  @Override
  public void write(@NotNull ProtocolBuffer buffer) {
    buffer.writeDouble(this.updateIntervalInSeconds);
    buffer.writeObjects(this.layouts);
    buffer.writeBoolean(this.knockBackEnabled);
    buffer.writeString(this.knockBackBypassPermission);
    buffer.writeDouble(this.knockBackDistance);
    buffer.writeDouble(this.knockBackStrength);
  }

  @Override
  public void read(@NotNull ProtocolBuffer buffer) {
    this.updateIntervalInSeconds = buffer.readDouble();
    this.layouts = buffer.readObjects(SignLayout.class);
    this.knockBackEnabled = buffer.readBoolean();
    this.knockBackBypassPermission = buffer.readString();
    this.knockBackDistance = buffer.readDouble();
    this.knockBackStrength = buffer.readDouble();
  }
}
