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
