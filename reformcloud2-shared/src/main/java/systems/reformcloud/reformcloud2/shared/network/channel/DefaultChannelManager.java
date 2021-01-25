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
package systems.reformcloud.reformcloud2.shared.network.channel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.channel.manager.ChannelManager;
import systems.reformcloud.reformcloud2.executor.api.utility.MoreCollections;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultChannelManager implements ChannelManager {

    private final Map<String, NetworkChannel> channels = new ConcurrentHashMap<>();

    @NotNull
    @Override
    public Optional<NetworkChannel> getChannel(@NotNull String name) {
        return Optional.ofNullable(this.channels.get(name));
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<NetworkChannel> getNetworkChannels(@NotNull String remoteAddress) {
        return MoreCollections.allOf(this.channels.values(), channel -> channel.getRemoteAddress().getHost().equals(remoteAddress));
    }

    @NotNull
    @Override
    public Optional<NetworkChannel> getFirstChannel() {
        Iterator<NetworkChannel> iterator = this.channels.values().iterator();
        return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<NetworkChannel> getRegisteredChannels() {
        return Collections.unmodifiableCollection(this.channels.values());
    }

    @Override
    public void registerChannel(@NotNull NetworkChannel channel) {
        this.channels.put(channel.getName(), channel);
    }

    @Override
    public void unregisterChannel(@NotNull String name) {
        this.channels.remove(name);
    }
}
