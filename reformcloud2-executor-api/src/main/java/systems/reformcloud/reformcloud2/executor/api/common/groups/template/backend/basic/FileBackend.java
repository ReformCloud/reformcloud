package systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.basic;

import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.TemplateBackend;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import javax.annotation.Nonnull;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileBackend implements TemplateBackend {

    public static final String NAME = "FILE";

    @Override
    public boolean existsTemplate(@Nonnull String group, @Nonnull String template) {
        return Files.exists(format(group, template));
    }

    @Override
    public void createTemplate(@Nonnull String group, @Nonnull String template) {
        if (!existsTemplate(group, template)) {
            SystemHelper.createDirectory(Paths.get("reformcloud/templates", group, template, "plugins"));
        }
    }

    @Nonnull
    @Override
    public Task<Void> loadTemplate(@Nonnull String group, @Nonnull String template, @Nonnull Path target) {
        if (!existsTemplate(group, template)) {
            createTemplate(group, template);
            return Task.completedTask(null);
        }

        SystemHelper.copyDirectory(format(group, template), target);
        return Task.completedTask(null);
    }

    @Nonnull
    @Override
    public Task<Void> loadGlobalTemplates(@Nonnull ProcessGroup group, @Nonnull Path target) {
        Streams.allOf(group.getTemplates(), Template::isGlobal).forEach(e -> loadTemplate(group.getName(), e.getName(), target));
        return Task.completedTask(null);
    }

    @Nonnull
    @Override
    public Task<Void> loadPath(@Nonnull String path, @Nonnull Path target) {
        File from = new File(path);
        if (from.isDirectory()) {
            SystemHelper.copyDirectory(from.toPath(), target);
        }

        return Task.completedTask(null);
    }

    @Override
    public void deployTemplate(@Nonnull String group, @Nonnull String template, @Nonnull Path current) {
        if (existsTemplate(group, template)) {
            SystemHelper.copyDirectory(current, format(group, template), Arrays.asList("log-out.log", "runner.jar"));
        }
    }

    @Override
    public void deleteTemplate(@Nonnull String group, @Nonnull String template) {
        if (!existsTemplate(group, template)) {
            return;
        }

        SystemHelper.deleteDirectory(format(group, template));
    }

    @Nonnull
    @Override
    public String getName() {
        return NAME;
    }

    private Path format(String group, String template) {
        return Paths.get("reformcloud/templates/" + group + "/" + template);
    }
}
