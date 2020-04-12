package systems.reformcloud.reformcloud2.runner.commands;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.Runner;
import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;

import java.util.Collection;

public final class StartApplicationCommand extends InterpreterCommand {

    public StartApplicationCommand(@NotNull Runner runner) {
        super("start_application");
        this.runner = runner;
    }

    private final Runner runner;

    @Override
    public void execute(@NotNull String cursorLine, @NotNull InterpretedReformScript script, @NotNull Collection<String> allLines) {
        this.runner.startApplication();
    }
}
