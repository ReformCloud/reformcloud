package systems.reformcloud.reformcloud2.runner.reformscript.basic;

import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterTask;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;

public final class RunnerInterpreterTask extends InterpreterTask {

    public RunnerInterpreterTask(@Nonnull String name, @Nonnull Map<String, InterpreterCommand> commands) {
        this.name = name;
        this.commands = commands;
    }

    private final String name;

    private final Map<String, InterpreterCommand> commands;

    @Nonnull
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void executeTask(@Nonnull String callerLine, @Nonnull InterpretedReformScript script, @Nonnull Collection<String> allLines) {
        for (Map.Entry<String, InterpreterCommand> commandEntry : commands.entrySet()) {
            commandEntry.getValue().execute(commandEntry.getKey(), script, allLines);
        }
    }
}
