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
package systems.reformcloud.group.template.version;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import systems.reformcloud.enums.EnumUtil;
import systems.reformcloud.network.data.ProtocolBuffer;

import java.util.Objects;

public final class DefaultVersion implements Version {

  private String installer;
  private String versionName;
  private String downloadUrl;
  private String configurator;
  private VersionType versionType;
  private int defaultStartPort;
  private boolean nativeTransportSupported;
  private JavaVersion requiredJavaVersion;
  private transient VersionInfo versionInfo;

  @ApiStatus.Internal
  public DefaultVersion() {
  }

  DefaultVersion(String installer, String configurator, String versionName, String downloadUrl, VersionType versionType, int defaultStartPort, boolean nativeTransportSupported) {
    this(installer, configurator, versionName, downloadUrl, versionType, defaultStartPort, nativeTransportSupported, null);
  }

  DefaultVersion(String installer, String configurator, String versionName, String downloadUrl, VersionType versionType, int defaultStartPort, boolean nativeTransportSupported, VersionInfo info) {
    this(installer, configurator, versionName, downloadUrl, versionType, defaultStartPort, nativeTransportSupported, JavaVersion.VERSION_1_8, info);
  }

  DefaultVersion(String installer, String configurator, String versionName, String downloadUrl, VersionType versionType,
                 int defaultStartPort, boolean nativeTransportSupported, JavaVersion requiredJavaVersion, VersionInfo info) {
    this.installer = installer;
    this.configurator = configurator;
    this.versionName = versionName;
    this.downloadUrl = downloadUrl;
    this.versionType = versionType;
    this.defaultStartPort = defaultStartPort;
    this.nativeTransportSupported = nativeTransportSupported;
    this.requiredJavaVersion = requiredJavaVersion;
    this.versionInfo = info == null ? VersionInfo.info(versionName) : info;
  }

  @Override
  public @NotNull String getDownloadUrl() {
    return this.downloadUrl;
  }

  @Override
  public void setDownloadUrl(@NotNull String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }

  @Override
  public @NotNull VersionType getVersionType() {
    return this.versionType;
  }

  @Override
  public void setVersionType(@NotNull VersionType versionType) {
    this.versionType = versionType;
  }

  @Override
  public @Range(from = 0, to = 65536) int getDefaultStartPort() {
    return this.defaultStartPort;
  }

  @Override
  public void setDefaultStartPort(@Range(from = 0, to = 65536) int defaultStartPort) {
    this.defaultStartPort = defaultStartPort;
  }

  @Override
  public boolean isNativeTransportSupported() {
    return this.nativeTransportSupported;
  }

  @Override
  public void setNativeTransportSupported(boolean nativeTransportSupported) {
    this.nativeTransportSupported = nativeTransportSupported;
  }

  @Override
  public @NotNull VersionInfo getInfo() {
    return this.versionInfo;
  }

  @Override
  public void setInfo(@NotNull VersionInfo versionInfo) {
    this.versionInfo = versionInfo;
  }

  @Override
  public @NotNull String getInstaller() {
    return this.installer;
  }

  @Override
  public void setInstaller(@NotNull String installer) {
    this.installer = installer;
  }

  @Override
  public @NotNull String getConfigurator() {
    return this.configurator;
  }

  @Override
  public void setConfigurator(@NotNull String configurator) {
    this.configurator = configurator;
  }

  @Override
  public @NotNull JavaVersion getRequiredJavaVersion() {
    return this.requiredJavaVersion;
  }

  @Override
  public void setRequiredJavaVersion(@NotNull JavaVersion version) {
    this.requiredJavaVersion = version;
  }

  @Override
  public boolean isCompatibleWithJvm() {
    return this.requiredJavaVersion.isCompatibleWithCurrent();
  }

  @Override
  public @NotNull Version clone() {
    return Version.version(this.versionName, this.configurator, this.downloadUrl, this.installer, this.versionType,
      this.defaultStartPort, this.nativeTransportSupported, this.requiredJavaVersion, this.versionInfo);
  }

  @Override
  public @NotNull String getName() {
    return this.versionName;
  }

  @Override
  public void setName(@NotNull String newName) {
    this.versionName = newName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    DefaultVersion that = (DefaultVersion) o;
    return Objects.equals(this.versionName, that.versionName) && this.versionType == that.versionType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.versionName, this.versionType);
  }

  @Override
  public void write(@NotNull ProtocolBuffer buffer) {
    buffer.writeString(this.installer);
    buffer.writeString(this.versionName);
    buffer.writeString(this.downloadUrl);
    buffer.writeString(this.configurator);
    buffer.writeByte(this.versionType.ordinal());
    buffer.writeInt(this.defaultStartPort);
    buffer.writeBoolean(this.nativeTransportSupported);
    buffer.writeByte(this.requiredJavaVersion.ordinal());
  }

  @Override
  public void read(@NotNull ProtocolBuffer buffer) {
    this.installer = buffer.readString();
    this.versionName = buffer.readString();
    this.downloadUrl = buffer.readString();
    this.configurator = buffer.readString();
    this.versionType = EnumUtil.findEnumFieldByIndex(VersionType.class, buffer.readByte()).orElse(null);
    this.defaultStartPort = buffer.readInt();
    this.nativeTransportSupported = buffer.readBoolean();
    this.requiredJavaVersion = EnumUtil.findEnumFieldByIndex(JavaVersion.class, buffer.readByte()).orElse(null);
  }
}
