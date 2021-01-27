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
package systems.reformcloud.reformcloud2.executor.api.group.process.startup;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;

import java.util.concurrent.TimeUnit;

public class DefaultAutomaticStartupConfiguration implements AutomaticStartupConfiguration {

  private boolean automaticStartupEnabled;
  private boolean automaticShutdownEnabled;
  private int maxPercentOfPlayersToStart;
  private int maxPercentOfPlayersToStop;
  private long checkIntervalInSeconds;

  protected DefaultAutomaticStartupConfiguration() {
  }

  protected DefaultAutomaticStartupConfiguration(boolean automaticStartupEnabled, boolean automaticShutdownEnabled) {
    this.automaticStartupEnabled = automaticStartupEnabled;
    this.automaticShutdownEnabled = automaticShutdownEnabled;
    this.maxPercentOfPlayersToStart = 80;
    this.maxPercentOfPlayersToStop = 80;
    this.checkIntervalInSeconds = 30;
  }

  protected DefaultAutomaticStartupConfiguration(boolean automaticStartupEnabled, boolean automaticShutdownEnabled, int maxPercentOfPlayersToStart,
                                                 int maxPercentOfPlayersToStop, long checkIntervalInSeconds) {
    this.automaticStartupEnabled = automaticStartupEnabled;
    this.automaticShutdownEnabled = automaticShutdownEnabled;
    this.maxPercentOfPlayersToStart = maxPercentOfPlayersToStart;
    this.maxPercentOfPlayersToStop = maxPercentOfPlayersToStop;
    this.checkIntervalInSeconds = checkIntervalInSeconds;
  }

  @Override
  public boolean isAutomaticStartupEnabled() {
    return this.automaticStartupEnabled;
  }

  @Override
  public void setAutomaticStartupEnabled(boolean automaticStartupEnabled) {
    this.automaticStartupEnabled = automaticStartupEnabled;
  }

  @Override
  public boolean isAutomaticShutdownEnabled() {
    return this.automaticShutdownEnabled;
  }

  @Override
  public void setAutomaticShutdownEnabled(boolean automaticShutdownEnabled) {
    this.automaticShutdownEnabled = automaticShutdownEnabled;
  }

  @Override
  public @Range(from = 0, to = 100) int getMaxPercentOfPlayersToStart() {
    return this.maxPercentOfPlayersToStart;
  }

  @Override
  public void setMaxPercentOfPlayersToStart(@Range(from = 0, to = 100) int maxPercentOfPlayersToStart) {
    this.maxPercentOfPlayersToStart = maxPercentOfPlayersToStart;
  }

  @Override
  public @Range(from = 0, to = 100) int getMaxPercentOfPlayersToStop() {
    return this.maxPercentOfPlayersToStop;
  }

  @Override
  public void setMaxPercentOfPlayersToStop(@Range(from = 0, to = 100) int maxPercentOfPlayersToStop) {
    this.maxPercentOfPlayersToStop = maxPercentOfPlayersToStop;
  }

  @Override
  public @Range(from = 0, to = Long.MAX_VALUE) long getCheckIntervalInSeconds() {
    return this.checkIntervalInSeconds;
  }

  @Override
  public void setCheckIntervalInSeconds(@Range(from = 0, to = Long.MAX_VALUE) long checkIntervalInSeconds) {
    this.checkIntervalInSeconds = checkIntervalInSeconds;
  }

  @Override
  public void setCheckInterval(@NotNull TimeUnit timeUnit, @Range(from = 0, to = Long.MAX_VALUE) long checkInterval) {
    this.checkIntervalInSeconds = timeUnit.toSeconds(checkInterval);
  }

  @Override
  public @NotNull AutomaticStartupConfiguration clone() {
    return new DefaultAutomaticStartupConfiguration(
      this.automaticStartupEnabled,
      this.automaticShutdownEnabled,
      this.maxPercentOfPlayersToStart,
      this.maxPercentOfPlayersToStop,
      this.checkIntervalInSeconds
    );
  }

  @Override
  public void write(@NotNull ProtocolBuffer buffer) {
    buffer.writeBoolean(this.automaticStartupEnabled);
    buffer.writeBoolean(this.automaticShutdownEnabled);
    buffer.writeInt(this.maxPercentOfPlayersToStart);
    buffer.writeInt(this.maxPercentOfPlayersToStop);
    buffer.writeLong(this.checkIntervalInSeconds);
  }

  @Override
  public void read(@NotNull ProtocolBuffer buffer) {
    this.automaticStartupEnabled = buffer.readBoolean();
    this.automaticShutdownEnabled = buffer.readBoolean();
    this.maxPercentOfPlayersToStart = buffer.readInt();
    this.maxPercentOfPlayersToStop = buffer.readInt();
    this.checkIntervalInSeconds = buffer.readLong();
  }
}
