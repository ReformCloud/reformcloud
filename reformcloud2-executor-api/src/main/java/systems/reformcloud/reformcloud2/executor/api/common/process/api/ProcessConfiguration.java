package systems.reformcloud.reformcloud2.executor.api.common.process.api;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

public class ProcessConfiguration {

    ProcessConfiguration(ProcessGroup base, UUID uniqueId, String displayName,
                         Integer maxMemory, Integer port, Template template, JsonConfiguration extra,
                         int id, int maxPlayers, Collection<ProcessInclusion> inclusions) {
        this.base = base;
        this.uniqueId = uniqueId == null ? UUID.randomUUID() : uniqueId;
        this.displayName = displayName;
        this.maxMemory = maxMemory;
        this.port = port;
        this.template = template;
        this.extra = extra;
        this.id = id;
        this.maxPlayers = maxPlayers;
        this.inclusions = inclusions;
    }

    private final ProcessGroup base;

    private final UUID uniqueId;

    private final String displayName;

    private final Integer maxMemory;

    private final Integer port;

    private final Template template;

    private final JsonConfiguration extra;

    private final int id;

    private final int maxPlayers;

    private final Collection<ProcessInclusion> inclusions;

    @Nonnull
    public ProcessGroup getBase() {
        return base;
    }

    @Nonnull
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    @Nullable
    public Integer getMaxMemory() {
        return maxMemory;
    }

    @Nullable
    public Integer getPort() {
        return port;
    }

    @Nullable
    public Template getTemplate() {
        return template;
    }

    @Nonnull
    public JsonConfiguration getExtra() {
        return extra;
    }

    public int getId() {
        return id;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Nonnull
    public Collection<ProcessInclusion> getInclusions() {
        return inclusions;
    }
}
