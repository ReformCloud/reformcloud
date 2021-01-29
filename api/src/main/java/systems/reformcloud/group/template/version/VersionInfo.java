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
import systems.reformcloud.network.data.SerializableObject;
import systems.reformcloud.functional.Sorted;
import systems.reformcloud.functional.Sorted3;

public interface VersionInfo extends SerializableObject, Sorted3<Integer, Integer, Integer>, Sorted<VersionInfo>, Cloneable {

  VersionInfo UNKNOWN = new DefaultVersionInfo(0, 0, 0);

  // PAPER_1_8_8 -> major = 1, minor = 8, patch = 8
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

  // classic string format 1.4 = major = 1, minor = 4; 1.5.6 = major = 1, minor = 5, patch = 6
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

  @Contract(value = "_, _, _ -> new", pure = true)
  static @NotNull VersionInfo info(int minor, int major, int patch) {
    return new DefaultVersionInfo(minor, major, patch);
  }

  int getMinor();

  int getMajor();

  int getPatch();

  @NotNull
  VersionInfo clone();

  @NotNull
  @Override
  String toString();
}
