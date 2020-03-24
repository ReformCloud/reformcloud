package systems.reformcloud.reformcloud2.runner.commands;

import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterTask;

import javax.annotation.Nonnull;
import java.util.Collection;

public final class ExecuteCommand extends InterpreterCommand {

    public ExecuteCommand() {
        super("execute");
    }

    @Override
    public void execute(@Nonnull String cursorLine, @Nonnull InterpretedReformScript script, @Nonnull Collection<String> allLines) {
        InterpreterTask task = script.getAllTasks()
                .stream()
                .filter(e -> e.getName().equals(cursorLine.replaceFirst(getCommand() + " ", "")))
                .findFirst()
                .orElse(null);
        if (task == null) {
            throw new UnsupportedOperationException(cursorLine + " tried to call task which does not exists");
        }

        task.executeTask(cursorLine, script, allLines);
    }
}
