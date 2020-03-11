package systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend;

import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import javax.annotation.Nonnull;
import java.nio.file.Path;

public interface TemplateBackend extends Nameable {

    boolean existsTemplate(String group, String template);

    void createTemplate(String group, String template);

    @Nonnull
    Task<Void> loadTemplate(String group, String template, Path target);

    @Nonnull
    Task<Void> loadGlobalTemplates(ProcessGroup group, Path target);

    @Nonnull
    Task<Void> loadPath(String path, Path target);

    void deployTemplate(String group, String template, Path current);

    void deleteTemplate(String group, String template);
}
