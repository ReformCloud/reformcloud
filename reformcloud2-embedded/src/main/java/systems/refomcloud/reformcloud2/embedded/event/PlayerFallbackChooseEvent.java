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
package systems.refomcloud.reformcloud2.embedded.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.event.Event;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.utility.optional.ReferencedOptional;

import java.util.Collection;
import java.util.UUID;

public class PlayerFallbackChooseEvent extends Event {

    public PlayerFallbackChooseEvent(UUID playerUniqueId, ProcessInformation filteredFallback, Collection<ProcessInformation> allLobbies) {
        this.playerUniqueId = playerUniqueId;
        this.filteredFallback = filteredFallback;
        this.allLobbies = allLobbies;
    }

    private final UUID playerUniqueId;

    private ProcessInformation filteredFallback;

    private final Collection<ProcessInformation> allLobbies;

    @NotNull
    public UUID getPlayerUniqueId() {
        return this.playerUniqueId;
    }

    @NotNull
    public ReferencedOptional<ProcessInformation> getFilteredFallback() {
        return ReferencedOptional.build(this.filteredFallback);
    }

    public void setFilteredFallback(@Nullable ProcessInformation filteredFallback) {
        this.filteredFallback = filteredFallback;
    }

    @NotNull
    public Collection<ProcessInformation> getAllLobbies() {
        return this.allLobbies;
    }
}
