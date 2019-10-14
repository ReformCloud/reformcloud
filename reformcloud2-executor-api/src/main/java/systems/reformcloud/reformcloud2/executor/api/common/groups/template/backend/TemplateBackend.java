package systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend;

import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

import java.nio.file.Path;

public interface TemplateBackend extends Nameable {

    boolean existsTemplate(String group, String template);

    void createTemplate(String group, String template);

    void loadTemplate(String group, String template, Path target);

    void deployTemplate(String group, String template, Path current);

    void deleteTemplate(String group, String template);
}
