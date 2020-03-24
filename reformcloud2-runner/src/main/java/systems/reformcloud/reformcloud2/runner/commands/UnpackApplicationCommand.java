package systems.reformcloud.reformcloud2.runner.commands;

import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;
import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;

import javax.annotation.Nonnull;
import java.util.Collection;

public final class UnpackApplicationCommand extends InterpreterCommand {

    public UnpackApplicationCommand() {
        super("unpack_application");
    }

    @Override
    public void execute(@Nonnull String cursorLine, @Nonnull InterpretedReformScript script, @Nonnull Collection<String> allLines) {
        RunnerUtils.copyCompiledFile("files/executor.jar", RunnerUtils.EXECUTOR_PATH);
    }
}
