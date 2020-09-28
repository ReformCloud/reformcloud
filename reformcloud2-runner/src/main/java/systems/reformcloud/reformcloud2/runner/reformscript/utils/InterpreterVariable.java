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
package systems.reformcloud.reformcloud2.runner.reformscript.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Represents a variable in a reform script
 */
public abstract class InterpreterVariable {

    private final String plain;

    /**
     * Creates a new variable
     *
     * @param plain The plain variable name which can get wrapped
     */
    public InterpreterVariable(@NotNull String plain) {
        this.plain = plain.toLowerCase();
    }

    /**
     * @return The wrapped name of the variable
     */
    @NotNull
    public String wrap() {
        return "_%_" + this.plain + "_%_";
    }

    /**
     * Unwraps the current variable and returns the replaced string
     *
     * @param cursorLine The current line of the cursor
     * @param fullLines  All lines of the script
     * @return The replaced string of the current variable
     */
    @NotNull
    public abstract String unwrap(@NotNull String cursorLine, @NotNull Collection<String> fullLines);
}
