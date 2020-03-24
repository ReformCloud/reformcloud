package systems.reformcloud.reformcloud2.runner.commands;

import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;
import systems.reformcloud.reformcloud2.runner.setup.RunnerExecutorSetup;

import javax.annotation.Nonnull;
import java.util.Collection;

public final class SetupCommand extends InterpreterCommand {

    public SetupCommand() {
        super("setup");
    }

    @Override
    public void execute(@Nonnull String cursorLine, @Nonnull InterpretedReformScript script, @Nonnull Collection<String> allLines) {
        RunnerExecutorSetup.executeSetup();
    }
}
