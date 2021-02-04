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
import systems.reformcloud.enums.EnumUtil;

public enum JavaVersion {
  /**
   * Java 8 major version. This is the first version as all java versions
   * before this release are unsupported by the system.
   */
  VERSION_1_8,
  /**
   * Java 9 major version.
   */
  VERSION_1_9,
  /**
   * Java 10 major version
   */
  VERSION_1_10,
  /**
   * Java 11 major version.
   */
  VERSION_11,
  /**
   * Java 12 major version.
   */
  VERSION_12,
  /**
   * Java 13 major version.
   */
  VERSION_13,
  /**
   * Java 14 major version.
   */
  VERSION_14,
  /**
   * Java 15 major version.
   */
  VERSION_15,
  /**
   * Java 16 major version.
   */
  @ApiStatus.Experimental
  VERSION_16,
  /**
   * Java 17 major version.
   */
  @ApiStatus.Experimental
  VERSION_17,
  /**
   * An unknown java version which is higher than 17.
   */
  @ApiStatus.Experimental
  VERSION_HIGHER;

  private static final int FIRST_MAJOR_VERSION_ORDINAL = 10;
  private static final JavaVersion CURRENT_JAVA_VERSION = detectCurrentVersion();

  private final String versionName;

  JavaVersion() {
    this.versionName = this.ordinal() >= FIRST_MAJOR_VERSION_ORDINAL ? this.getMajorVersion() : "1." + this.getMajorVersion();
  }

  /**
   * Loads the current java version of the jvm. This method should not be used at all.
   * Use {@link #current()} instead.
   *
   * @return The current java version of the jvm.
   */
  @NotNull
  private static JavaVersion detectCurrentVersion() {
    final double currentClassVersion = Double.parseDouble(System.getProperty("java.class.version"));
    final int versionIndex = (int) currentClassVersion - 44 - 8; // 44 cause Java 1 used class version 44, 8 cause Java 8 is the first version we support
    if (versionIndex < 0) {
      throw new IllegalArgumentException("Current class java version " + currentClassVersion + " is not supported");
    }
    if (versionIndex >= JavaVersion.values().length) {
      return JavaVersion.VERSION_HIGHER;
    }
    return EnumUtil.findEnumFieldByIndex(JavaVersion.class, versionIndex).orElseThrow(() -> new RuntimeException("Unexpected exception"));
  }

  /**
   * Get the current java version of the jvm.
   *
   * @return the current java version of the jvm.
   */
  @NotNull
  public static JavaVersion current() {
    return CURRENT_JAVA_VERSION;
  }

  /**
   * Tests if this java version os compatible with {@code otherVersion}.
   *
   * @param otherVersion The version to check if compatible with this version
   * @return {@code true} if this version is compatible, else {@code false}.
   */
  public boolean isCompatibleWith(@NotNull JavaVersion otherVersion) {
    return this.compareTo(otherVersion) >= 0;
  }

  /**
   * Tests if this java version is compatible with the current jvm version.
   *
   * @return if this java version is compatible with the current jvm version.
   */
  public boolean isCompatibleWithCurrent() {
    return this.compareTo(JavaVersion.current()) >= 0;
  }

  /**
   * Get the major version of this java version.
   *
   * @return the major version of this java version.
   */
  public String getMajorVersion() {
    return Integer.toString(this.ordinal() + 8);
  }

  @Override
  public String toString() {
    return this.versionName;
  }
}
