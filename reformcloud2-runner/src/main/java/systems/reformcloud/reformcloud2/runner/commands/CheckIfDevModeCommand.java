package systems.reformcloud.reformcloud2.runner.commands;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;
import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;

import java.util.Collection;

public final class CheckIfDevModeCommand extends InterpreterCommand {

    public CheckIfDevModeCommand() {
        super("check_if_dev_mode");
    }

    @Override
    public void execute(@NotNull String cursorLine, @NotNull InterpretedReformScript script, @NotNull Collection<String> allLines) {
        if (Integer.getInteger("reformcloud.executor.type", 0) != 3 && Boolean.getBoolean("reformcloud.dev.mode")) {
            RunnerUtils.deleteFileIfExists(RunnerUtils.EXECUTOR_PATH);
            System.out.println("Automatically deleted executor file at " + RunnerUtils.EXECUTOR_PATH.toString() + " because of dev mode");
        }
    }
}
