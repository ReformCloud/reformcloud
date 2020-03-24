package systems.reformcloud.reformcloud2.runner.commands;

import systems.reformcloud.reformcloud2.runner.Runner;
import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;

import javax.annotation.Nonnull;
import java.util.Collection;

public final class StartApplicationCommand extends InterpreterCommand {

    public StartApplicationCommand(@Nonnull Runner runner) {
        super("start_application");
        this.runner = runner;
    }

    private final Runner runner;

    @Override
    public void execute(@Nonnull String cursorLine, @Nonnull InterpretedReformScript script, @Nonnull Collection<String> allLines) {
        this.runner.startApplication();
    }
}
