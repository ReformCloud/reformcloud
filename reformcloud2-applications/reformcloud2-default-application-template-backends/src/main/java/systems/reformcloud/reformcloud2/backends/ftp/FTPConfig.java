/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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
package systems.reformcloud.reformcloud2.backends.ftp;

public final class FTPConfig {

    private final boolean sslEnabled;
    private final boolean enabled;
    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final String baseDirectory;

    public FTPConfig(boolean sslEnabled, boolean enabled, String host, int port, String user, String password, String baseDirectory) {
        this.sslEnabled = sslEnabled;
        this.enabled = enabled;
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.baseDirectory = baseDirectory;
    }

    public boolean isSslEnabled() {
        return sslEnabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }
}
