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
package systems.reformcloud.reformcloud2.runner.reformscript;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterVariable;

import java.io.File;
import java.nio.file.Path;

/**
 * Represents an interpreter for reform scripts. It can read the scripts and register placeholder as
 * well as commands for the script itself to run correctly
 */
public interface ReformScriptInterpreter {

    /**
     * Registers a command for the reform script which will get executed in the interpreted version
     * of the script ({@link InterpretedReformScript})
     *
     * @param command The command which should be registered
     * @return The same reform script interpreter instance as used to call the method
     */
    @NotNull
    ReformScriptInterpreter registerInterpreterCommand(@NotNull InterpreterCommand command);

    /**
     * Registers a variable which should be replaced if the variable pattern appears in the script code
     *
     * @param variable The variable which should be registered
     * @return The same reform script interpreter instance as used to call the method
     */
    @NotNull
    ReformScriptInterpreter registerInterpreterVariable(@NotNull InterpreterVariable variable);

    /**
     * Get the command by the given name
     *
     * @param command The name of the command which should get found
     * @return The command by the given name or {@code null} if the command is unknown
     */
    @Nullable
    InterpreterCommand getCommand(@NotNull String command);

    /**
     * Get the variable by the given name
     *
     * @param variable The name of the variable which should get found
     * @return The variable by the name or {@code null} if the variable is unknown
     */
    @Nullable
    InterpreterVariable getVariable(@NotNull String variable);

    /**
     * Interprets the given file as a reform script
     *
     * @param script The file name of the file which should get interpreted
     * @return The interpreted script or {@code null} if the interpreter cannot understand the file content
     */
    @Nullable
    default InterpretedReformScript interpret(@NotNull File script) {
        return this.interpret(script.toPath());
    }

    /**
     * Interprets the given path as a reform script
     *
     * @param script The path name of the file which should get interpreted
     * @return The interpreted script or {@code null} if the interpreter cannot understand the file content
     */
    @Nullable
    InterpretedReformScript interpret(@NotNull Path script);

}
