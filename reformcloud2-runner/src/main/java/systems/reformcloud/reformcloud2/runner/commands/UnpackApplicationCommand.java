package systems.reformcloud.reformcloud2.runner.commands;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;
import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;

import java.util.Collection;

public final class UnpackApplicationCommand extends InterpreterCommand {

    public UnpackApplicationCommand() {
        super("unpack_application");
    }

    @Override
    public void execute(@NotNull String cursorLine, @NotNull InterpretedReformScript script, @NotNull Collection<String> allLines) {
        RunnerUtils.copyCompiledFile("files/executor.jar", RunnerUtils.EXECUTOR_PATH);
    }
}
