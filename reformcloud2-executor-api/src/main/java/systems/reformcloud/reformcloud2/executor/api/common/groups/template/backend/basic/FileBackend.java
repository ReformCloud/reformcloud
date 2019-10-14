package systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.basic;

import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.TemplateBackend;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class FileBackend implements TemplateBackend {

    public static final String NAME = "FILE";

    @Override
    public boolean existsTemplate(String group, String template) {
        return Files.exists(format(group, template));
    }

    @Override
    public void createTemplate(String group, String template) {
        if (!existsTemplate(group, template)) {
            SystemHelper.createDirectory(format(group, template));
        }
    }

    @Override
    public void loadTemplate(String group, String template, Path target) {
        if (!existsTemplate(group, template)) {
            SystemHelper.createDirectory(format(group, template));
            return;
        }

        SystemHelper.copyDirectory(format(group, template), target);
    }

    @Override
    public void deployTemplate(String group, String template, Path current) {
        if (!existsTemplate(group, template)) {
            return;
        }

        SystemHelper.copyDirectory(current, format(group, template), Collections.singletonList("runner.jar"));
    }

    @Override
    public void deleteTemplate(String group, String template) {
        if (!existsTemplate(group, template)) {
            return;
        }

        SystemHelper.deleteDirectory(format(group, template));
    }

    @Override
    public String getName() {
        return NAME;
    }

    private Path format(String group, String template) {
        return Paths.get("reformcloud/templates/" + group + "/" + template);
    }
}
