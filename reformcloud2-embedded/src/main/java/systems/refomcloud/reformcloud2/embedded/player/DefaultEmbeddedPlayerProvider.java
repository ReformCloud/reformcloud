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
package systems.refomcloud.reformcloud2.embedded.player;

import org.jetbrains.annotations.NotNull;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.reformcloud.reformcloud2.executor.api.provider.PlayerProvider;
import systems.reformcloud.reformcloud2.executor.api.wrappers.PlayerWrapper;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetPlayerUniqueIdFromName;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetPlayerUniqueIdFromNameResult;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeIsPlayerOnlineByName;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeIsPlayerOnlineByUniqueId;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeIsPlayerOnlineResult;

import java.util.Optional;
import java.util.UUID;

public class DefaultEmbeddedPlayerProvider implements PlayerProvider {

    @Override
    public boolean isPlayerOnline(@NotNull String name) {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeIsPlayerOnlineByName(name))
            .map(result -> {
                if (result instanceof ApiToNodeIsPlayerOnlineResult) {
                    return ((ApiToNodeIsPlayerOnlineResult) result).isOnline();
                }

                return false;
            }).orElseGet(() -> false);
    }

    @Override
    public boolean isPlayerOnline(@NotNull UUID uniqueId) {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeIsPlayerOnlineByUniqueId(uniqueId))
            .map(result -> {
                if (result instanceof ApiToNodeIsPlayerOnlineResult) {
                    return ((ApiToNodeIsPlayerOnlineResult) result).isOnline();
                }

                return false;
            }).orElseGet(() -> false);
    }

    @NotNull
    @Override
    public Optional<PlayerWrapper> getPlayer(@NotNull String name) {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetPlayerUniqueIdFromName(name))
            .map(result -> {
                if (result instanceof ApiToNodeGetPlayerUniqueIdFromNameResult) {
                    UUID uniqueId = ((ApiToNodeGetPlayerUniqueIdFromNameResult) result).getUniqueId();
                    return Optional.<PlayerWrapper>of(new DefaultEmbeddedPlayerWrapper(uniqueId));
                }

                return Optional.<PlayerWrapper>empty();
            }).orElseGet(Optional::empty);
    }

    @NotNull
    @Override
    public Optional<PlayerWrapper> getPlayer(@NotNull UUID uniqueId) {
        return Optional.of(new DefaultEmbeddedPlayerWrapper(uniqueId));
    }
}
