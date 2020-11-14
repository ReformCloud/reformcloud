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
package systems.reformcloud.reformcloud2.shared.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.Element;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.adapter.JsonAdapter;

import java.io.Reader;
import java.lang.reflect.Type;

public class GsonAdapter implements JsonAdapter {

    private final Gson gson;

    public GsonAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public @NotNull Element toTree(@NotNull Object object) {
        final JsonElement backing = this.gson.toJsonTree(object);
        return ElementMapper.map(backing);
    }

    @Override
    public @NotNull Element toTree(@NotNull Object object, @NotNull Type type) {
        final JsonElement backing = this.gson.toJsonTree(object, type);
        return ElementMapper.map(backing);
    }

    @Override
    public @NotNull String toJson(@NotNull Object object) {
        return this.gson.toJson(object);
    }

    @Override
    public @NotNull String toJson(@NotNull Object object, @NotNull Type type) {
        return this.gson.toJson(object, type);
    }

    @Override
    public @NotNull String toJson(@NotNull Element element) {
        return this.gson.toJson(ElementMapper.map(element));
    }

    @Override
    public void toJson(@NotNull Element element, @NotNull Appendable writer) {
        this.gson.toJson(ElementMapper.map(element), writer);
    }

    @Override
    public @NotNull String toJson(@NotNull Element element, @NotNull Type type) {
        return this.gson.toJson(ElementMapper.map(element), type);
    }

    @Override
    public void toJson(@NotNull Object object, @NotNull Type type, @NotNull Appendable writer) {
        this.gson.toJson(object, type, writer);
    }

    @Override
    public <T> @NotNull T fromJson(@NotNull String json, @NotNull Class<T> type) {
        return this.gson.fromJson(json, type);
    }

    @Override
    public <T> @NotNull T fromJson(@NotNull String json, @NotNull Type type) {
        return this.gson.fromJson(json, type);
    }

    @Override
    public <T> @NotNull T fromJson(@NotNull Element element, @NotNull Class<T> type) {
        return this.gson.fromJson(ElementMapper.map(element), type);
    }

    @Override
    public <T> @NotNull T fromJson(@NotNull Element element, @NotNull Type type) {
        return this.gson.fromJson(ElementMapper.map(element), type);
    }

    @Override
    public <T> @NotNull T fromJson(@NotNull Reader reader, @NotNull Type type) {
        return this.gson.fromJson(reader, type);
    }
}
