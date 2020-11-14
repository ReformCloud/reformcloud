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
package systems.refomcloud.reformcloud2.embedded.config;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;

public final class EmbeddedConfig {

    private final String connectionHost;
    private final int connectionPort;
    private final String connectionKey;
    private final ProcessInformation processInformation;

    public EmbeddedConfig() {
        JsonConfiguration configuration = JsonConfiguration.read(".reformcloud/config.json");

        this.connectionHost = configuration.getString("host");
        this.connectionPort = configuration.getInteger("port");
        this.connectionKey = configuration.getString("key");
        this.processInformation = configuration.get("startInfo", ProcessInformation.TYPE);
    }

    @NotNull
    public String getConnectionHost() {
        return this.connectionHost;
    }

    public int getConnectionPort() {
        return this.connectionPort;
    }

    @NotNull
    public String getConnectionKey() {
        return this.connectionKey;
    }

    @NotNull
    public ProcessInformation getProcessInformation() {
        return this.processInformation;
    }
}
