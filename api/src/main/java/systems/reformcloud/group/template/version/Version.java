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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import systems.reformcloud.network.data.SerializableObject;
import systems.reformcloud.utility.name.ReNameable;

public interface Version extends ReNameable, SerializableObject, Cloneable {

  @NotNull
  @Contract(pure = true)
  static Version version(@NotNull String versionName,
                         @NotNull String downloadUrl,
                         @NotNull String installer,
                         @NotNull String configurator,
                         @NotNull VersionType versionType,
                         @Range(from = 0, to = 65536) int defaultStartPort,
                         boolean nativeTransportSupported
  ) {
    return new DefaultVersion(installer, configurator, versionName, downloadUrl, versionType, defaultStartPort, nativeTransportSupported);
  }

  @NotNull
  @Contract(pure = true)
  static Version version(@NotNull String versionName,
                         @NotNull String downloadUrl,
                         @NotNull String installer,
                         @NotNull String configurator,
                         @NotNull VersionType versionType,
                         @Range(from = 0, to = 65536) int defaultStartPort,
                         boolean nativeTransportSupported,
                         @Nullable VersionInfo versionInfo
  ) {
    return new DefaultVersion(installer, configurator, versionName, downloadUrl, versionType, defaultStartPort, nativeTransportSupported, versionInfo);
  }

  @NotNull
  String getDownloadUrl();

  void setDownloadUrl(@NotNull String downloadUrl);

  @NotNull
  VersionType getVersionType();

  void setVersionType(@NotNull VersionType versionType);

  @Range(from = 0, to = 65536)
  int getDefaultStartPort();

  void setDefaultStartPort(@Range(from = 0, to = 65536) int defaultStartPort);

  boolean isNativeTransportSupported();

  void setNativeTransportSupported(boolean nativeTransportSupported);

  @NotNull
  VersionInfo getInfo();

  void setInfo(@NotNull VersionInfo versionInfo);

  @NotNull
  String getInstaller();

  void setInstaller(@NotNull String installer);

  @NotNull
  String getConfigurator();

  void setConfigurator(@NotNull String configurator);

  @NotNull
  Version clone();

  @Override
  boolean equals(Object other);
}
