package systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.TemplateBackend;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public class FileBackend implements TemplateBackend {

    public static final String NAME = "FILE";

    @Override
    public boolean existsTemplate(@NotNull String group, @NotNull String template) {
        return Files.exists(format(group, template));
    }

    @Override
    public void createTemplate(@NotNull String group, @NotNull String template) {
        if (!existsTemplate(group, template)) {
            SystemHelper.createDirectory(Paths.get("reformcloud/templates", group, template, "plugins"));
        }
    }

    @NotNull
    @Override
    public Task<Void> loadTemplate(@NotNull String group, @NotNull String template, @NotNull Path target) {
        if (!existsTemplate(group, template)) {
            createTemplate(group, template);
            return Task.completedTask(null);
        }

        SystemHelper.copyDirectory(format(group, template), target);
        return Task.completedTask(null);
    }

    @NotNull
    @Override
    public Task<Void> loadGlobalTemplates(@NotNull ProcessGroup group, @NotNull Path target) {
        Streams.allOf(group.getTemplates(), Template::isGlobal).forEach(e -> loadTemplate(group.getName(), e.getName(), target));
        return Task.completedTask(null);
    }

    @NotNull
    @Override
    public Task<Void> loadPath(@NotNull String path, @NotNull Path target) {
        File from = new File(path);
        if (from.isDirectory()) {
            SystemHelper.copyDirectory(from.toPath(), target);
        }

        return Task.completedTask(null);
    }

    @Override
    public void deployTemplate(@NotNull String group, @NotNull String template, @NotNull Path current, @NotNull Collection<String> collection) {
        if (existsTemplate(group, template)) {
            SystemHelper.copyDirectory(current, format(group, template), collection);
        }
    }

    @Override
    public void deleteTemplate(@NotNull String group, @NotNull String template) {
        if (!existsTemplate(group, template)) {
            return;
        }

        SystemHelper.deleteDirectory(format(group, template));
    }

    @NotNull
    @Override
    public String getName() {
        return NAME;
    }

    private Path format(String group, String template) {
        return Paths.get("reformcloud/templates/" + group + "/" + template);
    }
}
