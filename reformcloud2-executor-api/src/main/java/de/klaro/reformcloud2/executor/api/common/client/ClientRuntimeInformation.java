package de.klaro.reformcloud2.executor.api.common.client;

import de.klaro.reformcloud2.executor.api.common.utility.name.Nameable;

public interface ClientRuntimeInformation extends Nameable {

    String startHost();

    int maxMemory();

    int maxProcessCount();
}
