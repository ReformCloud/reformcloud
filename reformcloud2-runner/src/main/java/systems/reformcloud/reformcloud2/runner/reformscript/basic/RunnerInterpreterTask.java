package systems.reformcloud.reformcloud2.runner.reformscript.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterTask;

import java.util.Collection;
import java.util.Map;

public final class RunnerInterpreterTask extends InterpreterTask {

    public RunnerInterpreterTask(@NotNull String name, @NotNull Map<String, InterpreterCommand> commands) {
        this.name = name;
        this.commands = commands;
    }

    private final String name;

    private final Map<String, InterpreterCommand> commands;

    @NotNull
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void executeTask(@NotNull String callerLine, @NotNull InterpretedReformScript script, @NotNull Collection<String> allLines) {
        for (Map.Entry<String, InterpreterCommand> commandEntry : commands.entrySet()) {
            commandEntry.getValue().execute(commandEntry.getKey(), script, allLines);
        }
    }
}
