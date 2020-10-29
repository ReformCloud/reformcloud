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
package systems.reformcloud.reformcloud2.executor.api.http;

import java.nio.charset.StandardCharsets;

/**
 * Represents all handleable http versions.
 *
 * @author derklaro
 * @since 25. October 2020
 */
public enum HttpVersion {
    /**
     * Represents HTTP 1.0.
     */
    HTTP_1_0("HTTP", 1, 0, false),
    /**
     * Represents HTTP 1.1 which is exactly like HTTP 1.0 but keep alive default is by default {@code true}.
     */
    HTTP_1_1("HTTP", 1, 1, true);

    private final String protocolName;
    private final int majorVersion;
    private final int minorVersion;
    private final String text;
    private final boolean keepAliveDefault;
    private final byte[] bytes;

    HttpVersion(String protocolName, int majorVersion, int minorVersion, boolean keepAliveDefault) {
        this.protocolName = protocolName.trim().toUpperCase();
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.text = this.protocolName + '/' + majorVersion + '.' + minorVersion;
        this.keepAliveDefault = keepAliveDefault;
        this.bytes = this.text.getBytes(StandardCharsets.US_ASCII);
    }

    public String getProtocolName() {
        return this.protocolName;
    }

    public int getMajorVersion() {
        return this.majorVersion;
    }

    public int getMinorVersion() {
        return this.minorVersion;
    }

    public String getText() {
        return this.text;
    }

    public boolean isKeepAliveDefault() {
        return this.keepAliveDefault;
    }

    public byte[] getBytes() {
        return this.bytes;
    }
}
