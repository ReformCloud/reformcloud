package systems.reformcloud.reformcloud2.executor.api.common.groups.template.inclusion;

public class Inclusion {

    public Inclusion(String key, String backend, InclusionLoadType inclusionLoadType) {
        this.key = key;
        this.backend = backend;
        this.inclusionLoadType = inclusionLoadType;
    }

    private final String key;

    private final String backend;

    private final InclusionLoadType inclusionLoadType;

    public String getKey() {
        return key;
    }

    public String getBackend() {
        return backend;
    }

    public InclusionLoadType getInclusionLoadType() {
        return inclusionLoadType;
    }

    public enum InclusionLoadType {

        PRE,

        PAST

    }
}
