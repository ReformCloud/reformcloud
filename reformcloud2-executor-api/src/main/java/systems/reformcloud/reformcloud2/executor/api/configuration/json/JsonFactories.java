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
package systems.reformcloud.reformcloud2.executor.api.configuration.json;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.adapter.JsonAdapterBuilder;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.types.Array;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.types.Null;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.types.Object;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.types.Primitive;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class JsonFactories {

    protected static final AtomicReference<JsonParser> DEFAULT_PARSER = new AtomicReference<>();
    protected static final AtomicReference<Supplier<JsonAdapterBuilder>> BUILDER_FACTORY = new AtomicReference<>();
    protected static final AtomicReference<Supplier<Object>> OBJECT_FACTORY = new AtomicReference<>();
    protected static final AtomicReference<Supplier<Array>> ARRAY_FACTORY = new AtomicReference<>();
    protected static final AtomicReference<Function<java.lang.Object, Primitive>> PRIMITIVE_FACTORY = new AtomicReference<>();
    protected static final AtomicReference<Supplier<Null>> NULL_FACTORY = new AtomicReference<>();

    protected JsonFactories() {
    }

    @NotNull
    public static JsonParser defaultParser() {
        return DEFAULT_PARSER.get();
    }

    @NotNull
    public static JsonAdapterBuilder newAdapterBuilder() {
        return BUILDER_FACTORY.get().get();
    }

    @NotNull
    public static Object newObject() {
        return OBJECT_FACTORY.get().get();
    }

    @NotNull
    public static Array newArray() {
        return ARRAY_FACTORY.get().get();
    }

    @NotNull
    public static Primitive newPrimitive(@NotNull Character character) {
        return PRIMITIVE_FACTORY.get().apply(character);
    }

    @NotNull
    public static Primitive newPrimitive(@NotNull Boolean bool) {
        return PRIMITIVE_FACTORY.get().apply(bool);
    }

    @NotNull
    public static Primitive newPrimitive(@NotNull Number integer) {
        return PRIMITIVE_FACTORY.get().apply(integer);
    }

    @NotNull
    public static Primitive newPrimitive(@NotNull String string) {
        return PRIMITIVE_FACTORY.get().apply(string);
    }

    @NotNull
    public static Null newNull() {
        return NULL_FACTORY.get().get();
    }
}
