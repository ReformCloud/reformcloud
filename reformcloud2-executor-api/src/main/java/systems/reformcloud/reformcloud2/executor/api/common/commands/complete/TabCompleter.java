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
package systems.reformcloud.reformcloud2.executor.api.common.commands.complete;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;

import java.util.Arrays;
import java.util.Collection;

/**
 * Represents any command which can be tab completed
 */
public interface TabCompleter {

    /**
     * Completes a command
     *
     * @param commandSource The command source of the command
     * @param commandLine   The command line with was given by the user
     * @param currentArg    The current arguments
     * @return The completed command arguments
     */
    @NotNull
    Collection<String> complete(@NotNull CommandSource commandSource, @NotNull String commandLine, @NotNull String[] currentArg);

    /**
     * Creates a collection of strings of an array
     *
     * @param strings The array which should get converted
     * @return The created collection of the given array
     */
    @NotNull
    default Collection<String> convert(@NotNull String... strings) {
        return Arrays.asList(strings);
    }
}
