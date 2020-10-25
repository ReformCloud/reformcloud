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
package systems.reformcloud.reformcloud2.executor.api.http;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface Headers extends Iterable<Map.Entry<String, String>> {

    @NotNull
    Optional<String> get(String name);

    @NotNull
    String get(String name, String defaultValue);

    @NotNull
    Optional<Integer> getInt(String name);

    int getInt(String name, int defaultValue);

    @NotNull
    Optional<Short> getShort(String name);

    short getShort(String name, short defaultValue);

    @NotNull
    Optional<Long> getTimeMillis(String name);

    long getTimeMillis(String name, long defaultValue);

    @NotNull
    List<String> getAll(String name);

    @NotNull
    List<Map.Entry<String, String>> entries();

    boolean contains(String name);

    boolean isEmpty();

    int size();

    @NotNull
    Set<String> names();

    @NotNull
    Headers add(@NotNull Headers headers);

    @NotNull
    Headers addInt(String name, int value);

    @NotNull
    Headers addShort(String name, short value);

    @NotNull
    Headers setAll(@NotNull Headers headers);

    @NotNull
    Headers setInt(String name, int value);

    @NotNull
    Headers setShort(String name, short value);

    @NotNull
    Headers remove(String name);

    @NotNull
    Headers clear();
}
