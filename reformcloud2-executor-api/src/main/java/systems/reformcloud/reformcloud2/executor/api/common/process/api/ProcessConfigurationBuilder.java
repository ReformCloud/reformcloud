package systems.reformcloud.reformcloud2.executor.api.common.process.api;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.RuntimeConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.backend.basic.FileBackend;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Duo;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;

import javax.annotation.Nonnull;
import java.util.*;

public final class ProcessConfigurationBuilder {

    @Nonnull
    public static ProcessConfigurationBuilder newBuilder(@Nonnull String processGroupName) {
        ProcessGroup group = ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().getProcessGroup(processGroupName);
        Conditions.nonNull(group, "Unable to find group with name " + processGroupName);
        return newBuilder(group);
    }

    @Nonnull
    public static ProcessConfigurationBuilder newBuilder(@Nonnull ProcessGroup processGroup) {
        return new ProcessConfigurationBuilder(processGroup);
    }

    /* =================================== */

    /**
     * Use {@link #newBuilder(String)} )} instead
     */
    private ProcessConfigurationBuilder(@Nonnull ProcessGroup processGroup) {
        this.base = processGroup;
        this.maxPlayers = processGroup.getPlayerAccessConfiguration().getMaxPlayers();
    }

    private final ProcessGroup base;

    private UUID uniqueID = UUID.randomUUID();

    private String displayName;

    private Integer maxMemory;

    private Integer port;

    private Template template;

    private JsonConfiguration extra = new JsonConfiguration();

    private int id = -1;

    private int maxPlayers;

    private Collection<Duo<String, String>> inclusions = new ArrayList<>();

    @Nonnull
    public ProcessConfigurationBuilder uniqueId(@Nonnull UUID uniqueID) {
        Conditions.nonNull(uniqueID, "Unable to set null unique id");

        this.uniqueID = uniqueID;
        return this;
    }

    @Nonnull
    public ProcessConfigurationBuilder id(int processId) {
        Conditions.isTrue(processId > 0, "Unable to start process with id smaller than 0");

        this.id = processId;
        return this;
    }

    @Nonnull
    public ProcessConfigurationBuilder displayName(@Nonnull String displayName) {
        Conditions.nonNull(displayName, "Unable to set null display name");

        this.displayName = displayName;
        return this;
    }

    @Nonnull
    public ProcessConfigurationBuilder maxMemory(int maxMemory) {
        Conditions.isTrue(maxMemory > 100, "Unable to start process with memory lower than 100MB");

        this.maxMemory = maxMemory;
        return this;
    }

    @Nonnull
    public ProcessConfigurationBuilder port(int port) {
        Conditions.isTrue(port > 0, "Unable to use port smaller than 0");

        this.port = port;
        return this;
    }

    @Nonnull
    public ProcessConfigurationBuilder template(@Nonnull Template template) {
        Conditions.nonNull(template, "Unable to use null template");

        this.template = template;
        return this;
    }

    @Nonnull
    public ProcessConfigurationBuilder template(@Nonnull String template) {
        Conditions.nonNull(template, "Unable to use null template");

        this.template = Streams.filterToReference(this.base.getTemplates(), e -> e.getName().equals(template)).orElse(this.randomTemplate());
        return this;
    }

    @Nonnull
    public ProcessConfigurationBuilder extra(@Nonnull JsonConfiguration extra) {
        Conditions.nonNull(extra, "Unable to set null extra data");

        this.extra = extra;
        return this;
    }

    @Nonnull
    public ProcessConfigurationBuilder maxPlayers(int maxPlayers) {
        Conditions.isTrue(maxPlayers > 0, "Unable to set max players to a value lower or equal to 0");

        this.maxPlayers = maxPlayers;
        return this;
    }

    @Nonnull
    public ProcessConfigurationBuilder inclusions(@Nonnull Collection<Duo<String, String>> inclusions) {
        Conditions.nonNull(inclusions, "Unable to set null inclusions");

        this.inclusions = inclusions;
        return this;
    }

    @Nonnull
    public ProcessConfigurationBuilder inclusion(@Nonnull Duo<String, String> inclusion) {
        Conditions.nonNull(inclusion, "Unable to add null inclusion");

        this.inclusions.add(inclusion);
        return this;
    }

    @Nonnull
    public ProcessConfiguration build() {
        return new ProcessConfiguration(
                this.base,
                this.uniqueID,
                this.displayName,
                this.maxMemory,
                this.port,
                this.template,
                this.extra,
                this.id,
                this.maxPlayers,
                this.inclusions
        );
    }


    private Template randomTemplate() {
        if (this.base.getTemplates().isEmpty()) {
            return new Template(0, "default", false, FileBackend.NAME, "#", new RuntimeConfiguration(
                    512, new ArrayList<>(), new HashMap<>()
            ), Version.PAPER_1_8_8);
        }

        if (this.base.getTemplates().size() == 1) {
            return this.base.getTemplates().get(0);
        }

        return this.base.getTemplates().get(new Random().nextInt(this.base.getTemplates().size()));
    }
}
