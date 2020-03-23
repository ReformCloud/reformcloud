package systems.reformcloud.reformcloud2.runner.reformscript.basic;

import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.ReformScriptInterpreter;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterTask;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterVariable;
import systems.reformcloud.reformcloud2.runner.util.KeyValueHolder;
import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    @Nonnull
    @Override
    public ReformScriptInterpreter registerInterpreterCommand(@Nonnull InterpreterCommand command) {
        this.commands.add(command);
        return this;
    }

    @Nonnull
    @Override
    public ReformScriptInterpreter registerInterpreterVariable(@Nonnull InterpreterVariable variable) {
        this.variables.add(variable);
        return this;
    }

    @Nullable
    @Override
    public InterpreterCommand getCommand(@Nonnull String command) {
        return this.commands
                .stream()
                .filter(e -> e.getCommand().equals(command.toUpperCase()))
                .findFirst()
                .orElse(null);
    }

    @Nullable
    @Override
    public InterpreterVariable getVariable(@Nonnull String variable) {
        return this.variables
                .stream()
                .filter(e -> e.wrap().equals(variable.toLowerCase())
                        || (e.wrap().endsWith("*_%_") && e.wrap().startsWith(variable)))
                .findFirst()
                .orElse(null);
    }

    @Nullable
    @Override
    public InterpretedReformScript interpret(@Nonnull Path script) {
        try {
            Collection<String> allLines = new CopyOnWriteArrayList<>(Files.readAllLines(script));
            Collection<String> comments = allLines
                    .stream()
                    .filter(e -> e.startsWith(COMMENT_LINE_START))
                    .collect(Collectors.toList());

            return this.interpret(allLines, comments);
        } catch (final IOException ex) {
            RunnerUtils.handleError("Unable to read reform script located at " + script.toString(), ex);
        }

        return null;
    }

    @Nullable
    private InterpretedReformScript interpret(@Nonnull Collection<String> allLines, @Nonnull Collection<String> comments) {
        Map<String, Map.Entry<Integer, InterpreterCommand>> commandsPerLine = new HashMap<>();
        int cursorPosition = 0;

        for (String line : allLines) {
            if (comments.contains(line)) {
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

                allLines.remove(line);
                allLines.add(this.replaceLineVariables(line, allLines));

                commandsPerLine.put(line, new KeyValueHolder<>(cursorPosition, command));
            } catch (final IllegalArgumentException ex) {
                RunnerUtils.handleError("Unable to handle script line " + cursorPosition, ex);
            }

            cursorPosition++;
        }

        Collection<InterpreterTask> tasks = this.parseTasks(allLines);
        if (commandsPerLine.isEmpty() && tasks.isEmpty()) {
            return null;
        }

        return new RunnerInterpretedReformScript(this, allLines, tasks, commandsPerLine);
    }

    @Nullable
    private InterpreterCommand getCommandOfLine(@Nonnull String line) throws IllegalArgumentException {
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

    @Nonnull
    private String replaceLineVariables(@Nonnull String line, @Nonnull Collection<String> allLines) {
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

    @Nonnull
    private Collection<InterpreterTask> parseTasks(@Nonnull Collection<String> allLines) throws IllegalArgumentException {
        Collection<InterpreterTask> tasks = new ArrayList<>();

        Collection<String> linesToParse = allLines
                .stream()
                .filter(e -> e.toUpperCase().startsWith(TASK_LINE_START))
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
                throw new IllegalArgumentException("Unexpected line " + line);
            }

            task.add(line.replaceFirst(TASK_LINE_START, ""));
        }

        if (!task.isEmpty()) {
            throw new IllegalArgumentException("Unclosed task: " + String.join("\n", task));
        }

        return tasks;
    }

    @Nullable
    private InterpreterTask parseTask(@Nonnull List<String> taskLines, @Nonnull Collection<String> allLines) {
        if (taskLines.isEmpty()) {
            return null;
        }

        String taskOpener = taskLines.remove(0);
        Map<String, InterpreterCommand> commandsPerLine = new HashMap<>();

        for (String taskLine : taskLines) {
            try {
                InterpreterCommand command = this.getCommandOfLine(taskLine);
                if (command == null) {
                    continue;
                }

                taskLines.remove(taskLine);
                taskLines.add(this.replaceLineVariables(taskLine, allLines));

                commandsPerLine.put(taskLine, command);
            } catch (final IllegalArgumentException ex) {
                RunnerUtils.handleError("Unable to handle script line " + taskLine, ex);
            }
        }

        return new RunnerInterpreterTask(taskOpener.replaceFirst(TASK_LINE_START, ""), commandsPerLine);
    }
}
