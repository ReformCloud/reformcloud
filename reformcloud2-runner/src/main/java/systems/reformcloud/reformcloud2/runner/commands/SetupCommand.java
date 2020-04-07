package systems.reformcloud.reformcloud2.runner.commands;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;
import systems.reformcloud.reformcloud2.runner.setup.RunnerExecutorSetup;

import java.util.Collection;

public final class SetupCommand extends InterpreterCommand {

    public SetupCommand() {
        super("setup");
    }

    @Override
    public void execute(@NotNull String cursorLine, @NotNull InterpretedReformScript script, @NotNull Collection<String> allLines) {
        if (Integer.getInteger("reformcloud.executor.type") != null) {
            return;
        }

        RunnerExecutorSetup.executeSetup();
    }
}
