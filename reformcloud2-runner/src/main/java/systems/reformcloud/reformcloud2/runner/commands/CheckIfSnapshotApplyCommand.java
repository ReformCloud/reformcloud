package systems.reformcloud.reformcloud2.runner.commands;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;
import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;

import java.util.Collection;

public final class CheckIfSnapshotApplyCommand extends InterpreterCommand {

    public CheckIfSnapshotApplyCommand() {
        super("check_if_snapshot_apply");
    }

    @Override
    public void execute(@NotNull String cursorLine, @NotNull InterpretedReformScript script, @NotNull Collection<String> allLines) {
        String indevBuildDownloadURL = System.getProperty("reformcloud.indev.build.url");

        if (Integer.getInteger("reformcloud.executor.type", 0) != 3
                && Boolean.getBoolean("reformcloud.indev.builds") && indevBuildDownloadURL != null) {
            System.out.println("Loading snapshot build from " + indevBuildDownloadURL + "...");
            RunnerUtils.downloadFile(indevBuildDownloadURL, RunnerUtils.EXECUTOR_PATH);
            System.out.println("Applied latest snapshot build to cloud system");
        }
    }
}
