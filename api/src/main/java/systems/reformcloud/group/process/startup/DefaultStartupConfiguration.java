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
package systems.reformcloud.group.process.startup;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import systems.reformcloud.network.data.ProtocolBuffer;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultStartupConfiguration implements StartupConfiguration {

  private int maximumProcessAmount;
  private int alwaysOnlineProcessAmount;
  private int alwaysPreparedProcessAmount;
  private String jvmCommand;
  private AutomaticStartupConfiguration startupConfiguration;
  private Collection<String> startingNodes;

  protected DefaultStartupConfiguration() {
  }

  protected DefaultStartupConfiguration(int maximumProcessAmount, int alwaysOnlineProcessAmount, int alwaysPreparedProcessAmount) {
    this(maximumProcessAmount, alwaysOnlineProcessAmount, alwaysPreparedProcessAmount, "java", AutomaticStartupConfiguration.disabled(), new ArrayList<>());
  }

  protected DefaultStartupConfiguration(int maximumProcessAmount, int alwaysOnlineProcessAmount, int alwaysPreparedProcessAmount,
                                        String jvmCommand, AutomaticStartupConfiguration startupConfiguration, Collection<String> startingNodes) {
    this.maximumProcessAmount = maximumProcessAmount;
    this.alwaysOnlineProcessAmount = alwaysOnlineProcessAmount;
    this.alwaysPreparedProcessAmount = alwaysPreparedProcessAmount;
    this.jvmCommand = jvmCommand;
    this.startupConfiguration = startupConfiguration;
    this.startingNodes = startingNodes;
  }

  @Override
  public @Range(from = 0, to = Integer.MAX_VALUE) int getMaximumProcessAmount() {
    return this.maximumProcessAmount;
  }

  @Override
  public void setMaximumProcessAmount(@Range(from = 0, to = Integer.MAX_VALUE) int maximumProcessAmount) {
    this.maximumProcessAmount = maximumProcessAmount;
  }

  @Override
  public @Range(from = 0, to = Integer.MAX_VALUE) int getAlwaysOnlineProcessAmount() {
    return this.alwaysOnlineProcessAmount;
  }

  @Override
  public void setAlwaysOnlineProcessAmount(@Range(from = 0, to = Integer.MAX_VALUE) int alwaysOnlineProcessAmount) {
    this.alwaysOnlineProcessAmount = alwaysOnlineProcessAmount;
  }

  @Override
  public @Range(from = 0, to = Integer.MAX_VALUE) int getAlwaysPreparedProcessAmount() {
    return this.alwaysPreparedProcessAmount;
  }

  @Override
  public void setAlwaysPreparedProcessAmount(@Range(from = 0, to = Integer.MAX_VALUE) int alwaysPreparedProcessAmount) {
    this.alwaysPreparedProcessAmount = alwaysPreparedProcessAmount;
  }

  @Override
  public @NotNull String getJvmCommand() {
    return this.jvmCommand;
  }

  @Override
  public void setJvmCommand(@NotNull String jvmCommand) {
    this.jvmCommand = jvmCommand;
  }

  @Override
  public @NotNull AutomaticStartupConfiguration getAutomaticStartupConfiguration() {
    return this.startupConfiguration;
  }

  @Override
  public void setAutomaticStartupConfiguration(@NotNull AutomaticStartupConfiguration automaticStartupConfiguration) {
    this.startupConfiguration = automaticStartupConfiguration;
  }

  @Override
  public @NotNull Collection<String> getStartingNodes() {
    return this.startingNodes;
  }

  @Override
  public void setStartingNodes(@NotNull Collection<String> startingNodes) {
    this.startingNodes = startingNodes;
  }

  @Override
  public void addStartingNode(@NotNull String node) {
    this.startingNodes.add(node);
  }

  @Override
  public void removeStartingNode(@NotNull String node) {
    this.startingNodes.remove(node);
  }

  @Override
  public @NotNull StartupConfiguration clone() {
    return new DefaultStartupConfiguration(
      this.maximumProcessAmount,
      this.alwaysOnlineProcessAmount,
      this.alwaysPreparedProcessAmount,
      this.jvmCommand,
      this.startupConfiguration.clone(),
      new ArrayList<>(this.startingNodes)
    );
  }

  @Override
  public void write(@NotNull ProtocolBuffer buffer) {
    buffer.writeInt(this.maximumProcessAmount);
    buffer.writeInt(this.alwaysOnlineProcessAmount);
    buffer.writeInt(this.alwaysPreparedProcessAmount);
    buffer.writeString(this.jvmCommand);
    buffer.writeObject(this.startupConfiguration);
    buffer.writeStringArray(this.startingNodes);
  }

  @Override
  public void read(@NotNull ProtocolBuffer buffer) {
    this.maximumProcessAmount = buffer.readInt();
    this.alwaysOnlineProcessAmount = buffer.readInt();
    this.alwaysPreparedProcessAmount = buffer.readInt();
    this.jvmCommand = buffer.readString();
    this.startupConfiguration = buffer.readObject(DefaultAutomaticStartupConfiguration.class);
    this.startingNodes = buffer.readStringArray();
  }
}
