package systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a template backend which manages the template for all groups
 */
public interface TemplateBackend extends Nameable {

    /**
     * Checks if the specified template for the group exists
     *
     * @param group    The group of which the template should be
     * @param template The name of the template which should get checked
     * @return If the specified template exists in the group
     */
    boolean existsTemplate(@NotNull String group, @NotNull String template);

    /**
     * Creates a new template for the group
     *
     * @param group    The name of the group the template should be for
     * @param template The name of the template which should get created
     */
    void createTemplate(@NotNull String group, @NotNull String template);

    /**
     * Loads a template from a group to the specified path
     *
     * @param group    The group name of the group in which the template is located
     * @param template The name of the template which should get loaded
     * @param target   The path the template should get copied to
     * @return A task which will get completed after the successful template copy to the path
     */
    @NotNull
    Task<Void> loadTemplate(@NotNull String group, @NotNull String template, @NotNull Path target);

    /**
     * Loads all global templates from the specified group
     *
     * @param group  The group in which the template is located
     * @param target The path the global template(s) should get copied to
     * @return A task which will get completed when all templates are copied
     * @see Template#isGlobal()
     */
    @NotNull
    Task<Void> loadGlobalTemplates(@NotNull ProcessGroup group, @NotNull Path target);

    /**
     * Loads a specific path
     *
     * @param path   The path which should get loaded
     * @param target The target path to which the specified path should get copied
     * @return A task which get completed after the successful copy
     */
    @NotNull
    Task<Void> loadPath(@NotNull String path, @NotNull Path target);

    /**
     * Deploys the specified template of the specified group from the current operating path
     *
     * @param group    The group in which the template is located
     * @param template The name of the template which should get deployed
     * @param current  The current operating path which should get deployed
     */
    default void deployTemplate(@NotNull String group, @NotNull String template, @NotNull Path current) {
        this.deployTemplate(group, template, current, new ArrayList<>());
    }

    /**
     * Deploys the specified template of the specified group from the current operating path
     *
     * @param group    The group in which the template is located
     * @param template The name of the template which should get deployed
     * @param current  The current operating path which should get deployed
     * @param excluded The file names of the excluded files which should not get deployed
     */
    void deployTemplate(@NotNull String group, @NotNull String template,
                        @NotNull Path current, @NotNull Collection<String> excluded);

    /**
     * Deletes the specified template
     *
     * @param group    The name of the group the template is located in
     * @param template The name of the template which should get deleted
     */
    void deleteTemplate(@NotNull String group, @NotNull String template);
}
