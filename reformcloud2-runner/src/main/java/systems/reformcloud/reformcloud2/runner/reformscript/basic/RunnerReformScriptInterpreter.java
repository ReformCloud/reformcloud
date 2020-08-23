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
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.ReformScriptInterpreter;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterTask;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterVariable;
import systems.reformcloud.reformcloud2.runner.util.KeyValueHolder;
import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public final class RunnerReformScriptInterpreter implements ReformScriptInterpreter {

    private static final String COMMENT_LINE_START = "# ";
    private static final String TASK_LINE_START = "TASK ";
    private static final String TASK_END = "TASK END";

    private final Collection<InterpreterCommand> commands = new CopyOnWriteArrayList<>();
    private final Collection<InterpreterVariable> variables = new CopyOnWriteArrayList<>();

    @NotNull
    @Override
    public ReformScriptInterpreter registerInterpreterCommand(@NotNull InterpreterCommand command) {
        this.commands.add(command);
        return this;
    }

    @NotNull
    @Override
    public ReformScriptInterpreter registerInterpreterVariable(@NotNull InterpreterVariable variable) {
        this.variables.add(variable);
        return this;
    }

    @Nullable
    @Override
    public InterpreterCommand getCommand(@NotNull String command) {
        return this.commands
                .stream()
                .filter(e -> e.getCommand().equals(command.toUpperCase()))
                .findFirst()
                .orElse(null);
    }

    @Nullable
    @Override
    public InterpreterVariable getVariable(@NotNull String variable) {
        return this.variables
                .stream()
                .filter(e -> variable.toLowerCase().contains(e.wrap()) || (e.wrap().endsWith("*_%_") && e.wrap().startsWith(variable)))
                .findFirst()
                .orElse(null);
    }

    @Nullable
    @Override
    public InterpretedReformScript interpret(@NotNull Path script) {
        try {
            List<String> allLines = new CopyOnWriteArrayList<>(Files.readAllLines(script));
            Collection<String> comments = allLines
                    .stream()
                    .filter(e -> e.startsWith(COMMENT_LINE_START))
                    .collect(Collectors.toList());

            return this.interpret(script, allLines, comments);
        } catch (final IOException ex) {
            RunnerUtils.handleError("Unable to read reform script located at " + script.toString(), ex);
        }

        return null;
    }

    @Nullable
    private InterpretedReformScript interpret(@NotNull Path path, @NotNull List<String> allLines,
                                              @NotNull Collection<String> comments) {
        Map<String, Map.Entry<Integer, InterpreterCommand>> commandsPerLine = new LinkedHashMap<>();
        int cursorPosition = 0;

        for (String line : allLines) {
            if (comments.contains(line) || line.trim().isEmpty()) {
                cursorPosition++;
                continue;
            }

            if (line.startsWith(TASK_LINE_START)) {
                cursorPosition++;
                continue;
            }

            try {
                InterpreterCommand command = this.getCommandOfLine(line);
                if (command == null) {
                    cursorPosition++;
                    continue;
                }

                int index = allLines.indexOf(line);
                if (index != -1) {
                    allLines.remove(index);
                    allLines.add(index, line = this.replaceLineVariables(line, allLines));
                }

                commandsPerLine.put(line, new KeyValueHolder<>(cursorPosition, command));
            } catch (final IllegalArgumentException ex) {
                RunnerUtils.handleError("Unable to handle script line " + cursorPosition + ": " + line, ex);
            }

            cursorPosition++;
        }

        Collection<InterpreterTask> tasks = this.parseTasks(allLines);
        if (commandsPerLine.isEmpty() && tasks.isEmpty()) {
            return null;
        }

        return new RunnerInterpretedReformScript(this, path, allLines, tasks, commandsPerLine);
    }

    @Nullable
    private InterpreterCommand getCommandOfLine(@NotNull String line) throws IllegalArgumentException {
        String[] arguments = line.split(" ");
        if (arguments.length == 0) {
            return null;
        }

        InterpreterCommand result = this.getCommand(arguments[0]);
        if (result == null) {
            throw new IllegalArgumentException("Unable to find command by name " + arguments[0]);
        }

        return result;
    }

    @NotNull
    private String replaceLineVariables(@NotNull String line, @NotNull Collection<String> allLines) {
        String[] arguments = line.split(" ");
        if (arguments.length <= 1) {
            return line;
        }

        arguments = Arrays.copyOfRange(arguments, 1, arguments.length);
        for (String argument : arguments) {
            InterpreterVariable variable = this.getVariable(argument);
            if (variable == null) {
                continue;
            }

            line = line.replace(argument, variable.unwrap(line, allLines));
        }

        return line;
    }

    @NotNull
    private Collection<InterpreterTask> parseTasks(@NotNull Collection<String> allLines) throws IllegalArgumentException {
        Collection<InterpreterTask> tasks = new ArrayList<>();

        Collection<String> linesToParse = allLines
                .stream()
                .filter(e -> e.startsWith(TASK_LINE_START))
                .collect(Collectors.toList());

        List<String> task = new CopyOnWriteArrayList<>();
        for (String line : linesToParse) {
            if (line.equals(TASK_END)) {
                tasks.add(this.parseTask(task, allLines));
                task.clear();
                continue;
            }

            if (line.startsWith(COMMENT_LINE_START) || line.trim().isEmpty()) {
                continue;
            }

            if (!line.startsWith(TASK_LINE_START)) {
                throw new IllegalArgumentException("Invalid task line " + line + "(expected \"" + TASK_LINE_START + "\")");
            }

            task.add(line.replaceFirst(TASK_LINE_START, ""));
        }

        if (!task.isEmpty()) {
            throw new IllegalArgumentException("Unclosed task: " + String.join("\n", task));
        }

        return tasks;
    }

    @Nullable
    private InterpreterTask parseTask(@NotNull List<String> taskLines, @NotNull Collection<String> allLines) {
        if (taskLines.isEmpty()) {
            return null;
        }

        String taskOpener = taskLines.remove(0);
        Map<String, InterpreterCommand> commandsPerLine = new LinkedHashMap<>();

        for (String taskLine : taskLines) {
            try {
                InterpreterCommand command = this.getCommandOfLine(taskLine);
                if (command == null) {
                    continue;
                }

                int index = taskLines.indexOf(taskLine);
                if (index != -1) {
                    taskLines.remove(index);
                    taskLines.add(index, taskLine = this.replaceLineVariables(taskLine, allLines));
                }

                commandsPerLine.put(taskLine, command);
            } catch (final IllegalArgumentException ex) {
                RunnerUtils.handleError("Unable to handle script line " + taskLine, ex);
            }
        }

        return new RunnerInterpreterTask(taskOpener, commandsPerLine);
    }
}
