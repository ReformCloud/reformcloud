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
package systems.reformcloud.reformcloud2.executor.api.groups.template.version;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;

public final class DefaultVersionInfo implements VersionInfo {

    private int major;
    private int minor;
    private int patch;

    DefaultVersionInfo() {
    }

    DefaultVersionInfo(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    @Override
    public int getMajor() {
        return this.major;
    }

    @Override
    public int getMinor() {
        return this.minor;
    }

    @Override
    public int getPatch() {
        return this.patch;
    }

    @Override
    public @NotNull VersionInfo clone() {
        return VersionInfo.info(this.major, this.minor, this.patch);
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeInt(this.major);
        buffer.writeInt(this.minor);
        buffer.writeInt(this.patch);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.major = buffer.readInt();
        this.minor = buffer.readInt();
        this.patch = buffer.readInt();
    }

    @Override
    public @NotNull String toString() {
        return this.major + "." + this.minor + "." + this.patch;
    }

    @Override
    public int compareTo(@NotNull VersionInfo info) {
        return this.compareTo(info.getMajor(), info.getMinor(), info.getPatch());
    }

    @Override
    public int compareTo(@NotNull Integer major, @NotNull Integer minor, @NotNull Integer patch) {
        if (major > minor) {
            if (major.equals(patch)) {
                return 0;
            } else if (major > patch) {
                return 1;
            } else {
                return -1;
            }
        } else {
            if (major.equals(minor)) {
                return 0;
            } else if (minor > patch) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
