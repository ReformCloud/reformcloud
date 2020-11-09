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
package systems.reformcloud.reformcloud2.executor.api.configuration;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;

import java.util.Optional;

public abstract class DefaultJsonDataHolder<T extends JsonDataHolder<T>> implements JsonDataHolder<T> {

    protected JsonConfiguration dataHolder;

    protected DefaultJsonDataHolder() {
        this(new JsonConfiguration());
    }

    protected DefaultJsonDataHolder(JsonConfiguration dataHolder) {
        this.dataHolder = dataHolder;
    }

    @Override
    public @NotNull <V> Optional<V> get(@NotNull String key, @NotNull Class<V> type) {
        return Optional.ofNullable(this.dataHolder.get(key, type));
    }

    @Override
    public @NotNull <V> T add(@NotNull String key, @NotNull V value) {
        if (!this.has(key)) {
            this.dataHolder.add(key, value);
        }
        return this.self();
    }

    @Override
    public @NotNull <V> T set(@NotNull String key, @NotNull V value) {
        this.dataHolder.add(key, value);
        return this.self();
    }

    @Override
    public @NotNull T remove(@NotNull String key) {
        this.dataHolder.remove(key);
        return this.self();
    }

    @Override
    public boolean has(@NotNull String key) {
        return this.dataHolder.has(key);
    }

    @Override
    public @NotNull JsonConfiguration getData() {
        return this.dataHolder;
    }

    @Override
    public void setData(@NotNull JsonConfiguration data) {
        this.dataHolder = data;
    }

    @Override
    public void clearData() {
        this.dataHolder.clear();
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.dataHolder.toPrettyString());
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.dataHolder = new JsonConfiguration(buffer.readString());
    }

    @NotNull
    public abstract T self();

    @NotNull
    @Override
    public abstract T clone();
}
