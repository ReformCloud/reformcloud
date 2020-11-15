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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.Element;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.types.Array;

import java.util.Iterator;
import java.util.Optional;

public class GsonArray extends GsonElement implements Array {

    private final JsonArray array;

    public GsonArray(JsonArray gsonElement) {
        super(gsonElement);
        this.array = gsonElement;
    }

    @Override
    public @NotNull Array add(@Nullable Boolean bool) {
        this.array.add(bool);
        return this;
    }

    @Override
    public @NotNull Array add(@Nullable Character character) {
        this.array.add(character);
        return this;
    }

    @Override
    public @NotNull Array add(@Nullable Number number) {
        this.array.add(number);
        return this;
    }

    @Override
    public @NotNull Array add(@Nullable String string) {
        this.array.add(string);
        return this;
    }

    @Override
    public @NotNull Array add(@Nullable Element element) {
        this.array.add(ElementMapper.map(element));
        return this;
    }

    @Override
    public @NotNull Array addAll(@NotNull Array array) {
        this.array.addAll(ElementMapper.map(array).getAsJsonArray());
        return this;
    }

    @Override
    public @NotNull Optional<Element> set(int index, @NotNull Element element) throws ArrayIndexOutOfBoundsException {
        final JsonElement before = this.array.set(index, ElementMapper.map(element));
        return Optional.ofNullable(before == null ? null : ElementMapper.map(before));
    }

    @Override
    public boolean remove(@NotNull Element element) {
        return this.array.remove(ElementMapper.map(element));
    }

    @Override
    public @NotNull Optional<Element> remove(int index) {
        final JsonElement before = this.array.remove(index);
        return Optional.ofNullable(before == null ? null : ElementMapper.map(before));
    }

    @Override
    public boolean contains(@NotNull Element element) {
        return this.array.contains(ElementMapper.map(element));
    }

    @Override
    public int size() {
        return this.array.size();
    }

    @Override
    public @NotNull Optional<Element> get(int i) {
        return Optional.of(ElementMapper.map(this.array.get(i)));
    }

    @Override
    public @NotNull Array clone() {
        return new GsonArray(this.array.deepCopy());
    }

    @NotNull
    @Override
    public Iterator<Element> iterator() {
        return new ArrayIterator(this.array.iterator());
    }

    private static final class ArrayIterator implements Iterator<Element> {

        private final Iterator<JsonElement> backing;

        private ArrayIterator(Iterator<JsonElement> backing) {
            this.backing = backing;
        }

        @Override
        public boolean hasNext() {
            return this.backing.hasNext();
        }

        @Override
        public Element next() {
            return ElementMapper.map(this.backing.next());
        }
    }
}
