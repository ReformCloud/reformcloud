package systems.reformcloud.reformcloud2.executor.api.common.network.auth.defaults;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.Auth;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.NetworkType;

import javax.annotation.Nonnull;

public final class DefaultAuth implements Auth {

    public DefaultAuth(String key, String parent, NetworkType type, String name, JsonConfiguration extra) {
        this.key = key;
        this.parent = parent;
        this.type = type;
        this.name = name;
        this.extra = extra;
    }

    private final String key;

    private final String parent;

    private final NetworkType type;

    private final String name;

    private final JsonConfiguration extra;

    @Nonnull
    @Override
    public String key() {
        return key;
    }

    @Override
    public String parent() {
        return parent;
    }

    @Nonnull
    @Override
    public NetworkType type() {
        return type;
    }

    @Nonnull
    @Override
    public JsonConfiguration extra() {
        return extra;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }
}
