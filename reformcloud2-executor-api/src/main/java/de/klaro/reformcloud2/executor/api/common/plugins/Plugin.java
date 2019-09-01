package de.klaro.reformcloud2.executor.api.common.plugins;

import de.klaro.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.List;

public abstract class Plugin implements Nameable {

    public abstract int version();

    public abstract String author();

    public abstract String main();

    public abstract List<String> depends();

    public abstract List<String> softpends();

    public abstract boolean enabled();
}
