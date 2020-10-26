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
package systems.reformcloud.reformcloud2.node.http.request;

import io.netty.channel.Channel;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.http.request.HttpRequestSource;

import java.net.SocketAddress;

public class DefaultHttpRequestSource implements HttpRequestSource {

    private final Channel channel;

    public DefaultHttpRequestSource(Channel channel) {
        this.channel = channel;
    }

    @Override
    public @NotNull String id() {
        return this.channel.id().asLongText();
    }

    @Override
    public boolean isOpen() {
        return this.channel.isOpen();
    }

    @Override
    public boolean isActive() {
        return this.channel.isActive();
    }

    @Override
    public boolean isWritable() {
        return this.channel.isWritable();
    }

    @Override
    public long bytesBeforeUnwritable() {
        return this.channel.bytesBeforeUnwritable();
    }

    @Override
    public long bytesBeforeWritable() {
        return this.channel.bytesBeforeWritable();
    }

    @Override
    public @NotNull SocketAddress serverAddress() {
        return this.channel.localAddress();
    }

    @Override
    public @NotNull SocketAddress clientAddress() {
        return this.channel.remoteAddress();
    }

    @Override
    public void close() {
        this.channel.close();
    }
}
