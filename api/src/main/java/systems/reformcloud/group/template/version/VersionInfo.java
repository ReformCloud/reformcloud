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
import systems.reformcloud.functional.Sorted;
import systems.reformcloud.functional.Sorted3;
import systems.reformcloud.network.data.SerializableObject;

/**
 * A version info about a {@link Version}.
 */
public interface VersionInfo extends SerializableObject, Sorted3<Integer, Integer, Integer>, Sorted<VersionInfo>, Cloneable {
  /**
   * A version info for all proxy versions.
   */
  VersionInfo UNKNOWN = new DefaultVersionInfo(0, 0, 0);

  /**
   * Creates a version from a version string. The input should look like {@code PAPER_1_8} or
   * {@code PAPER_1_8_8}. Another possibility is for example {@code PAPER} which will result in
   * {@link #UNKNOWN}. If the version is not formatted like one of the examples, an exception will
   * be thrown.
   *
   * @param versionName The version name to parse.
   * @return The resulting version info from the {@code versionName}.
   */
  @Contract(value = "_ -> new", pure = true)
  static @NotNull VersionInfo info(@NotNull String versionName) {
    String[] parts = versionName.split("_");
    if (parts.length == 1) {
      return UNKNOWN;
    }

    if (parts.length < 3 || parts.length > 4) {
      throw new IllegalStateException("VersionName " + versionName + " does not match required part split NAME_major_minor or NAME_major_minor_patch");
    }

    try {
      int major = Integer.parseInt(parts[1]);
      int minor = Integer.parseInt(parts[2]);
      int patch = parts.length > 3 ? Integer.parseInt(parts[3]) : 0;
      return new DefaultVersionInfo(major, minor, patch);
    } catch (NumberFormatException exception) {
      return UNKNOWN;
    }
  }

  /**
   * Parses a classic version string like {@code 1.8} or {@code 1.8.8}. The other possibility
   * is for example just {@code 1} which will always result in {@link #UNKNOWN}.
   *
   * @param versionString The version name to parse.
   * @return The resulting version info from the {@code versionString}.
   */
  @Contract(value = "_ -> new", pure = true)
  static @NotNull VersionInfo fromVersionString(@NotNull String versionString) {
    String[] parts = versionString.split("\\.");
    if (parts.length == 1) {
      return UNKNOWN;
    }

    if (parts.length < 2 || parts.length > 3) {
      throw new IllegalStateException("VersionString " + versionString + " does not match required part split major.minor or major.minor.patch");
    }

    try {
      int major = Integer.parseInt(parts[0]);
      int minor = Integer.parseInt(parts[1]);
      int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
      return new DefaultVersionInfo(major, minor, patch);
    } catch (NumberFormatException exception) {
      return UNKNOWN;
    }
  }

  /**
   * Creates a version info from the provided {@code minor}, {@code major} and {@code patch} version.
   *
   * @param minor The minor of the version.
   * @param major The major of the version.
   * @param patch The patch of the version.
   * @return The created version info.
   */
  @Contract(value = "_, _, _ -> new", pure = true)
  static @NotNull VersionInfo info(int minor, int major, int patch) {
    return new DefaultVersionInfo(minor, major, patch);
  }

  /**
   * Get the minor version number.
   *
   * @return The minor version number.
   */
  int getMinor();

  /**
   * Get the major version number.
   *
   * @return The major version number.
   */
  int getMajor();

  /**
   * Get the patch version number.
   *
   * @return The patch version number.
   */
  int getPatch();

  /**
   * Creates a clone of this version info.
   *
   * @return A clone of this version info.
   */
  @NotNull
  VersionInfo clone();

  /**
   * Creates a string from the version info. The formatting is {@code minor.major.patch}.
   *
   * @return A string from the version info.
   */
  @NotNull
  @Override
  String toString();
}
