package de.klaro.reformcloud2.executor.api.common.plugins;

import de.klaro.reformcloud2.executor.api.common.utility.name.Nameable;

import java.util.List;

public interface Plugin extends Nameable {

    int version();

    String author();

    String main();

    List<String> depends();

    List<String> softpends();

    boolean enabled();
}
