package systems.reformcloud.reformcloud2.runner.commands;

import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;
import systems.reformcloud.reformcloud2.runner.util.RunnerUtils;

import javax.annotation.Nonnull;
import java.util.Collection;

public final class CheckIfSnapshotApplyCommand extends InterpreterCommand {

    public CheckIfSnapshotApplyCommand() {
        super("check_if_snapshot_apply");
    }

    @Override
    public void execute(@Nonnull String cursorLine, @Nonnull InterpretedReformScript script, @Nonnull Collection<String> allLines) {
        String indevBuildDownloadURL = System.getProperty("reformcloud.indev.build.url");

        if (Integer.getInteger("reformcloud.executor.type", 0) != 3
                && Boolean.getBoolean("reformcloud.indev.builds") && indevBuildDownloadURL != null) {
            System.out.println("Loading snapshot build from " + indevBuildDownloadURL + "...");
            RunnerUtils.downloadFile(indevBuildDownloadURL, RunnerUtils.EXECUTOR_PATH);
            System.out.println("Applied latest snapshot build to cloud system");
        }
    }
}
