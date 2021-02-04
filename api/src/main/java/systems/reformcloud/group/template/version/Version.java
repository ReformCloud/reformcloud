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

/**
 * A version which is loadable.
 */
public interface Version extends ReNameable, SerializableObject, Cloneable {

  /**
   * Creates a new version.
   *
   * @param versionName              The name of the version to create.
   * @param downloadUrl              The download url of the version.
   * @param installer                The installer of the version.
   * @param configurator             The configurator of the version.
   * @param versionType              The version type of the version.
   * @param defaultStartPort         The default start port of the version.
   * @param nativeTransportSupported If native (epoll) transport is supported by the version.
   * @return The created version.
   */
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

  /**
   * Creates a new version.
   *
   * @param versionName              The name of the version to create.
   * @param downloadUrl              The download url of the version.
   * @param installer                The installer of the version.
   * @param configurator             The configurator of the version.
   * @param versionType              The version type of the version.
   * @param defaultStartPort         The default start port of the version.
   * @param nativeTransportSupported If native (epoll) transport is supported by the version.
   * @param versionInfo              The version info about the version.
   * @return The created version.
   */
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

  /**
   * Creates a new version.
   *
   * @param versionName              The name of the version to create.
   * @param downloadUrl              The download url of the version.
   * @param installer                The installer of the version.
   * @param configurator             The configurator of the version.
   * @param versionType              The version type of the version.
   * @param defaultStartPort         The default start port of the version.
   * @param nativeTransportSupported If native (epoll) transport is supported by the version.
   * @param requiredJavaVersion      The minimum required java version for running this version.
   * @param versionInfo              The version info about the version.
   * @return The created version.
   */
  @NotNull
  @Contract(pure = true)
  static Version version(@NotNull String versionName,
                         @NotNull String downloadUrl,
                         @NotNull String installer,
                         @NotNull String configurator,
                         @NotNull VersionType versionType,
                         @Range(from = 0, to = 65536) int defaultStartPort,
                         boolean nativeTransportSupported,
                         @NotNull JavaVersion requiredJavaVersion,
                         @Nullable VersionInfo versionInfo
  ) {
    return new DefaultVersion(installer, configurator, versionName, downloadUrl, versionType, defaultStartPort, nativeTransportSupported, requiredJavaVersion, versionInfo);
  }

  /**
   * Get the download url of the version.
   *
   * @return The download url of the version.
   */
  @NotNull
  String getDownloadUrl();

  /**
   * Sets the download url of the version.
   *
   * @param downloadUrl The download url to use.
   */
  void setDownloadUrl(@NotNull String downloadUrl);

  /**
   * Get the version type of this version.
   *
   * @return The version type of this version.
   */
  @NotNull
  VersionType getVersionType();

  /**
   * Sets the version type of this version.
   *
   * @param versionType The version type of this version.
   */
  void setVersionType(@NotNull VersionType versionType);

  /**
   * Get the default start port of this version.
   *
   * @return the default start port of this version.
   */
  @Range(from = 0, to = 65536)
  int getDefaultStartPort();

  /**
   * Sets the default start port of this version.
   *
   * @param defaultStartPort The default start port to use.
   */
  void setDefaultStartPort(@Range(from = 0, to = 65536) int defaultStartPort);

  /**
   * Get if native transport (most likely "epoll") is supported by this version.
   *
   * @return If epoll is supported by this version.
   */
  boolean isNativeTransportSupported();

  /**
   * Sets if native transport is supported by this version.
   *
   * @param nativeTransportSupported If native transport is supported by this version.
   */
  void setNativeTransportSupported(boolean nativeTransportSupported);

  /**
   * Get the version info of this version.
   *
   * @return The version info of this version.
   */
  @NotNull
  VersionInfo getInfo();

  /**
   * Sets the version info of this version.
   *
   * @param versionInfo The version info to use.
   */
  void setInfo(@NotNull VersionInfo versionInfo);

  /**
   * Gets the installer of this version.
   *
   * @return The installer of this version.
   */
  @NotNull
  String getInstaller();

  /**
   * Sets the installer of this version.
   *
   * @param installer The installer to use.
   */
  void setInstaller(@NotNull String installer);

  /**
   * Gets the configurator of this version.
   *
   * @return The configurator of this version.
   */
  @NotNull
  String getConfigurator();

  /**
   * Sets the configurator of this version.
   *
   * @param configurator The configurator to use.
   */
  void setConfigurator(@NotNull String configurator);

  /**
   * Gets the minimum required java version for running this version.
   *
   * @return the minimum required java version for running this version.
   */
  @NotNull
  JavaVersion getRequiredJavaVersion();

  /**
   * Sets the minimum required java version for running this version.
   *
   * @param version the minimum required java version for running this version.
   */
  void setRequiredJavaVersion(@NotNull JavaVersion version);

  /**
   * Checks if this version is compatible to run in the current jvm.
   *
   * @return if this version is compatible to run in the current jvm.
   */
  boolean isCompatibleWithJvm();

  /**
   * Creates a clone of this version.
   *
   * @return A clone of this version.
   */
  @NotNull
  Version clone();

  /**
   * Checks if the given object is equal to this version.
   *
   * @param other The object to check.
   * @return {@code true} if the given {@code other} object is equal to this one, else {@code false}.
   */
  @Override
  boolean equals(Object other);
}
