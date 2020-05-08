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
package systems.reformcloud.reformcloud2.runner.reformscript.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.ReformScriptInterpreter;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterTask;
import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

public final class RunnerInterpretedReformScript implements InterpretedReformScript {

    RunnerInterpretedReformScript(@NotNull ReformScriptInterpreter parent,
                                  @NotNull Path file,
                                  @NotNull Collection<String> allLines,
                                  @NotNull Collection<InterpreterTask> tasks,
                                  @NotNull Map<String, Map.Entry<Integer, InterpreterCommand>> commandsPerLine) {
        this.parent = parent;
        this.file = file;
        this.allLines = allLines;
        this.tasks = tasks;
        this.commandsPerLine = commandsPerLine;
    }

    private final Path file;

    private final ReformScriptInterpreter parent;

    private final Collection<String> allLines;

    private final Collection<InterpreterTask> tasks;

    private final Map<String, Map.Entry<Integer, InterpreterCommand>> commandsPerLine;

    @NotNull
    @Override
    public ReformScriptInterpreter getInterpreter() {
        return this.parent;
    }

    @NotNull
    @Override
    public Collection<InterpreterTask> getAllTasks() {
        return this.tasks;
    }

    @NotNull
    @Override
    public Path getScriptPath() {
        return this.file;
    }

    @Override
    public void execute() {
        for (String line : allLines) {
            RunnerUtils.debug("Executing script line " + line);

            Map.Entry<Integer, InterpreterCommand> entry = commandsPerLine.get(line);
            if (entry == null) {
                continue;
            }

            RunnerUtils.debug("Executing cursor line " + entry.getKey() + "...");
            entry.getValue().execute(line, this, allLines);
            RunnerUtils.debug("Executed cursor line " + entry.getKey());
        }
    }
}
