package systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.basic;

import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.TemplateBackend;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class FileBackend implements TemplateBackend {

    public static final String NAME = "FILE";

    @Override
    public boolean existsTemplate(String group, String template) {
        return Files.exists(format(group, template));
    }

    @Nonnull
    @Override
    public CompletableFuture<Void> createTemplate(String group, String template) {
        if (!existsTemplate(group, template)) {
            SystemHelper.createDirectory(Paths.get("reformcloud/templates", group, template, "plugins"));
        }

        return CompletableFuture.completedFuture(null);
    }

    @Nonnull
    @Override
    public CompletableFuture<Void> loadTemplate(String group, String template, Path target) {
        if (!existsTemplate(group, template)) {
            createTemplate(group, template);
            return CompletableFuture.completedFuture(null);
        }

        SystemHelper.copyDirectory(format(group, template), target);
        return CompletableFuture.completedFuture(null);
    }

    @Nonnull
    @Override
    public CompletableFuture<Void> loadGlobalTemplates(ProcessGroup group, Path target) {
        Streams.allOf(group.getTemplates(), Template::isGlobal).forEach(e -> loadTemplate(group.getName(), e.getName(), target));
        return CompletableFuture.completedFuture(null);
    }

    @Nonnull
    @Override
    public CompletableFuture<Void> deployTemplate(String group, String template, Path current) {
        if (!existsTemplate(group, template)) {
            return CompletableFuture.completedFuture(null);
        }

        SystemHelper.copyDirectory(current, format(group, template), Arrays.asList("log-out.log", "runner.jar"));
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void deleteTemplate(String group, String template) {
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
