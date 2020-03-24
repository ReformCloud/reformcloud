package systems.reformcloud.reformcloud2.runner.commands;

import systems.reformcloud.reformcloud2.runner.Runner;
import systems.reformcloud.reformcloud2.runner.reformscript.InterpretedReformScript;
import systems.reformcloud.reformcloud2.runner.reformscript.utils.InterpreterCommand;

import javax.annotation.Nonnull;
import java.util.Collection;

public final class CheckForUpdatesCommand extends InterpreterCommand {

    public CheckForUpdatesCommand(@Nonnull Runner runner) {
        super("check_for_updates");
        this.runner = runner;
    }

    private final Runner runner;

    @Override
    public void execute(@Nonnull String cursorLine, @Nonnull InterpretedReformScript script, @Nonnull Collection<String> allLines) {
        if (Integer.getInteger("reformcloud.executor.type", 0) == 3
                || !Boolean.getBoolean("reformcloud.auto.update")
                || Boolean.getBoolean("reformcloud.indev.builds")
                || Boolean.getBoolean("reformcloud.dev.mode")) {
            System.out.println("Automatic apply of updates is disabled!");
            return;
        }

        System.out.println("Collecting information about updates...");
        runner.getApplicationsUpdater().collectInformation();
        runner.getCloudVersionUpdater().collectInformation();
        System.out.println("Collected all needed information");

        if (runner.getCloudVersionUpdater().hasNewVersion()) {
            System.out.println("The " + runner.getCloudVersionUpdater().getName() + " updater has a new version available");
            runner.getCloudVersionUpdater().applyUpdates();
        }

        if (runner.getApplicationsUpdater().hasNewVersion()) {
            System.out.println("The " + runner.getApplicationsUpdater().getName() + " updater has a new version available");
            runner.getApplicationsUpdater().applyUpdates();
        }
    }
}
