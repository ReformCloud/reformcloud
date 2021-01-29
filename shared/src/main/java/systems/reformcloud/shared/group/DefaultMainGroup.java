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
package systems.reformcloud.shared.group;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.configuration.data.DefaultJsonDataHolder;
import systems.reformcloud.group.main.MainGroup;
import systems.reformcloud.network.data.ProtocolBuffer;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultMainGroup extends DefaultJsonDataHolder<MainGroup> implements MainGroup {

  private Collection<String> subGroups;
  private String name;

  DefaultMainGroup() {
  }

  public DefaultMainGroup(Collection<String> subGroups, String name) {
    this.subGroups = subGroups;
    this.name = name;
  }

  @Override
  public @NotNull @UnmodifiableView Collection<String> getSubGroups() {
    return this.subGroups;
  }

  @Override
  public void setSubGroups(@NotNull Collection<String> subGroups) {
    this.subGroups = subGroups;
  }

  @Override
  public void addSubGroup(@NotNull String subGroup) {
    this.subGroups.add(subGroup);
  }

  @Override
  public void removeSubGroup(@NotNull String subGroup) {
    this.subGroups.remove(subGroup);
  }

  @Override
  public boolean hasSubGroup(@NotNull String name) {
    return this.subGroups.contains(name);
  }

  @Override
  public void removeAllSubGroups() {
    this.subGroups.clear();
  }

  @Override
  public void update() {
    ExecutorAPI.getInstance().getMainGroupProvider().updateMainGroup(this);
  }

  @Override
  public @NotNull MainGroup clone() {
    return new DefaultMainGroup(new ArrayList<>(this.subGroups), this.name);
  }

  @Override
  public void write(@NotNull ProtocolBuffer buffer) {
    buffer.writeStringArray(this.subGroups);
    buffer.writeString(this.name);
  }

  @Override
  public void read(@NotNull ProtocolBuffer buffer) {
    this.subGroups = buffer.readStringArray();
    this.name = buffer.readString();
  }

  @Override
  public @NotNull MainGroup self() {
    return this;
  }

  @Override
  public @NotNull String getName() {
    return this.name;
  }
}
