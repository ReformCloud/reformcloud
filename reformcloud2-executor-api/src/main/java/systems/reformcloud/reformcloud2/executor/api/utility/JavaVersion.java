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
package systems.reformcloud.reformcloud2.executor.api.utility;

import org.jetbrains.annotations.NotNull;

public enum JavaVersion {

    VERSION_1_1,
    VERSION_1_2,
    VERSION_1_3,
    VERSION_1_4,
    VERSION_1_5,
    VERSION_1_6,
    VERSION_1_7,
    VERSION_1_8,
    VERSION_1_9,
    VERSION_1_10,
    VERSION_11,
    VERSION_12,
    VERSION_13,
    VERSION_14,
    VERSION_15,
    VERSION_16,
    VERSION_17,

    VERSION_UNKNOWN;

    private static final int FIRST_MAJOR_VERSION_ORDINAL = 10;
    private static final JavaVersion CURRENT = forClassVersion(Math.round(Float.parseFloat(System.getProperty("java.class.version"))));
    private final String versionName;

    JavaVersion() {
        this.versionName = this.ordinal() >= FIRST_MAJOR_VERSION_ORDINAL ? this.getMajorVersion() : "1." + this.getMajorVersion();
    }

    public boolean isCompatibleWith(@NotNull JavaVersion otherVersion) {
        return this.compareTo(otherVersion) >= 0;
    }

    private @NotNull String getName() {
        return this.versionName;
    }

    public @NotNull String getMajorVersion() {
        return Integer.toString(this.ordinal() + 1);
    }

    @Override
    public String toString() {
        return this.versionName;
    }

    private static JavaVersion getVersionForMajor(int major) {
        return major >= values().length || major <= 0 ? JavaVersion.VERSION_UNKNOWN : values()[major - 1];
    }

    public static JavaVersion current() {
        return CURRENT;
    }

    public static JavaVersion forClassVersion(int classVersion) {
        return getVersionForMajor(classVersion - 44);
    }
}
