package systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend;

import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public interface TemplateBackend extends Nameable {

    boolean existsTemplate(String group, String template);

    @Nonnull
    CompletableFuture<Void> createTemplate(String group, String template);

    @Nonnull
    CompletableFuture<Void> loadTemplate(String group, String template, Path target);

    @Nonnull
    CompletableFuture<Void> loadGlobalTemplates(ProcessGroup group, Path target);

    @Nonnull
    CompletableFuture<Void> deployTemplate(String group, String template, Path current);

    void deleteTemplate(String group, String template);
}
