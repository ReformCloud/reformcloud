package de.klaro.reformcloud2.executor.api.common.network.auth;

import de.klaro.reformcloud2.executor.api.common.utility.Nameable;

public interface Auth extends Nameable {

    String key();

    String parent();

    NetworkType type();
}
