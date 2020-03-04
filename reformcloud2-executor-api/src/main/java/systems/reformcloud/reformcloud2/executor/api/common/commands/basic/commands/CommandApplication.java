package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.LoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.common.application.updater.ApplicationRemoteUpdate;
import systems.reformcloud.reformcloud2.executor.api.common.application.updater.ApplicationUpdateRepository;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public final class CommandApplication extends GlobalCommand {

    public CommandApplication() {
        super("applications", "reformcloud.command.applications", "Manage the applications", "apps", "modules");
    }

    @Override
    public void describeCommandToSender(@Nonnull CommandSource source) {
        source.sendMessages((
                "applications [list]          | Lists all applications\n" +
                        "applications [update]        | Fetches and downloads all available application updates\n" +
                        "applications <name> [info]   | Shows information about a specific application\n" +
                        "applications <name> [update] | Updates a specific application"
        ).split("\n"));
    }

    @Override
    public boolean handleCommand(@Nonnull CommandSource commandSource, @Nonnull String[] strings) {
        if (strings.length == 0) {
            this.describeCommandToSender(commandSource);
            return true;
        }

        if (strings[0].equalsIgnoreCase("list")) {
            this.listApplicationsToSender(commandSource);
            return true;
        }

        if (strings[0].equalsIgnoreCase("update")) {
            List<LoadedApplication> applications = ExecutorAPI.getInstance().getSyncAPI().getApplicationSyncAPI().getApplications();
            if (applications == null) {
                return true;
            }

            int doneUpdates = 0;
            for (LoadedApplication application : applications) {
                Application app = application.loader().getInternalApplication(application.getName());
                if (app == null) {
                    continue;
                }

                ApplicationUpdateRepository updateRepository = app.getUpdateRepository();
                if (updateRepository == null) {
                    commandSource.sendMessage(LanguageManager.get("command-applications-app-no-updater", application.getName()));
                    continue;
                }

                if (updateRepository.isNewVersionAvailable() && updateRepository.getUpdate() != null) {
                    commandSource.sendMessage(LanguageManager.get(
                            "command-applications-version-available",
                            updateRepository.getUpdate().getNewVersion(),
                            application.getName()
                    ));
                    this.doUpdate(app.getApplication(), updateRepository.getUpdate());
                    doneUpdates++;
                }
            }

            commandSource.sendMessage(LanguageManager.get("command-applications-suffix", doneUpdates));
            return true;
        }

        LoadedApplication target = ExecutorAPI.getInstance().getSyncAPI().getApplicationSyncAPI().getApplication(strings[0]);
        if (target == null) {
            commandSource.sendMessage(LanguageManager.get("command-applications-app-not-found", strings[0]));
            return true;
        }

        if (strings.length == 2 && strings[1].equalsIgnoreCase("info")) {
            this.describeApplicationToSender(commandSource, target);
            return true;
        }

        if (strings.length == 2 && strings[1].equalsIgnoreCase("update")) {
            ApplicationRemoteUpdate update = this.getUpdate(target);
            if (update == null) {
                commandSource.sendMessage(LanguageManager.get("command-applications-app-no-update", target.getName()));
                return true;
            }

            commandSource.sendMessage(LanguageManager.get(
                    "command-applications-version-available",
                    update.getNewVersion(),
                    target.getName()
            ));
            this.doUpdate(target, update);
            commandSource.sendMessage(LanguageManager.get("command-applications-app-update-done", target.getName()));
            return true;
        }

        this.describeCommandToSender(commandSource);
        return true;
    }

    private void listApplicationsToSender(CommandSource source) {
        List<LoadedApplication> applications = ExecutorAPI.getInstance().getSyncAPI().getApplicationSyncAPI().getApplications();
        if (applications == null) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Applications (").append(applications.size()).append(")").append("\n");
        for (LoadedApplication application : applications) {
            builder.append(" > Name        - ").append(application.getName()).append("\n");
            builder.append(" > Status      - ").append(application.applicationStatus().name()).append("\n");
            builder.append(" > Author      - ").append(application.applicationConfig().author()).append("\n");
            builder.append(" \n");
        }

        source.sendMessages(builder.toString().split("\n"));
    }

    private void describeApplicationToSender(CommandSource source, LoadedApplication application) {
        AtomicReference<StringBuilder> builder = new AtomicReference<>(new StringBuilder());
        builder.get().append(" > Name        - ").append(application.getName()).append("\n");
        builder.get().append(" > Status      - ").append(application.applicationStatus().name()).append("\n");
        builder.get().append(" > Version     - ").append(application.applicationConfig().version()).append("\n");
        builder.get().append(" > API-Version - ").append(application.applicationConfig().implementedVersion()).append("\n");
        builder.get().append(" > Update      - ").append(getUpdate(application) != null ? "&cavailable&r" : "&cup-to-date&r").append("\n");
        builder.get().append(" > Author      - ").append(application.applicationConfig().author()).append("\n");
        builder.get().append(" > Description - ").append(application.applicationConfig().description()).append("\n");
        builder.get().append(" > Website     - ").append(application.applicationConfig().website()).append("\n");
        builder.get().append(" > Location    - ").append(application.applicationConfig().applicationFile().getPath()).append("\n");
        source.sendMessages(builder.get().toString().split("\n"));
    }

    @Nullable
    private ApplicationRemoteUpdate getUpdate(LoadedApplication application) {
        Application app = application.loader().getInternalApplication(application.getName());
        if (app == null) {
            return null;
        }

        ApplicationUpdateRepository updateRepository = app.getUpdateRepository();
        return updateRepository == null || updateRepository.getUpdate() == null ? null : updateRepository.getUpdate();
    }

    private void doUpdate(LoadedApplication application, ApplicationRemoteUpdate update) {
        SystemHelper.createDirectory(Paths.get("reformcloud/.update/apps"));
        String fileName = application.applicationConfig().applicationFile().getName();
        String[] split = fileName.split("-");
        String name = fileName
                .replace("-SNAPSHOT", "")
                .replace("-" + split[split.length - 1], "")
                .replace(".jar", "");

        DownloadHelper.downloadAndDisconnect(
                update.getDownloadUrl(),
                "reformcloud/.update/apps/" + name + "-" + update.getNewVersion() + ".jar"
        );
    }
}
