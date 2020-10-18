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
package systems.reformcloud.reformcloud2.node.network;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.network.channel.EndpointChannelReader;
import systems.reformcloud.reformcloud2.shared.network.client.DefaultNetworkClient;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public final class NodeNetworkClient extends DefaultNetworkClient {

    public static final Collection<String> CONNECTIONS = new CopyOnWriteArrayList<>();

    @Override
    public boolean connect(@NotNull String host, int port, @NotNull Supplier<EndpointChannelReader> supplier) {
        if (CONNECTIONS.stream().anyMatch(host::equals)) {
            return false;
        }

        if (super.connect(host, port, supplier)) {
            CONNECTIONS.add(host);
            return true;
        }

        return false;
    }

    @Override
    public void disconnect() {
        CONNECTIONS.clear();
        super.disconnect();
    }
}
