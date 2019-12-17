package systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend;

import java.nio.file.Path;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;

public interface TemplateBackend extends Nameable {

  boolean existsTemplate(String group, String template);

  void createTemplate(String group, String template);

  void loadTemplate(String group, String template, Path target);

  void loadGlobalTemplates(ProcessGroup group, Path target);

  void deployTemplate(String group, String template, Path current);

  void deleteTemplate(String group, String template);
}
