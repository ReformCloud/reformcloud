/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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
package systems.reformcloud.reformcloud2.shared.group;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.builder.MainGroupBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public abstract class DefaultMainGroupBuilder implements MainGroupBuilder {

    protected String name;
    protected Collection<String> subGroups = new ArrayList<>();

    @NotNull
    @Override
    public MainGroupBuilder name(@NotNull String name) {
        this.name = name;
        return this;
    }

    @NotNull
    @Override
    public MainGroupBuilder subGroups(String... subGroups) {
        this.subGroups = Arrays.asList(subGroups);
        return this;
    }

    @NotNull
    @Override
    public MainGroupBuilder subGroups(@NotNull Collection<String> subGroups) {
        this.subGroups = subGroups;
        return this;
    }
}
