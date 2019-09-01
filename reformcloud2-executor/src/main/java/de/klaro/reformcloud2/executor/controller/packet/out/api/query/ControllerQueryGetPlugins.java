package de.klaro.reformcloud2.executor.controller.packet.out.api.query;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ControllerQueryGetPlugins extends DefaultPacket {

    public ControllerQueryGetPlugins() {
        super(99, new JsonConfiguration());
    }

    public ControllerQueryGetPlugins(String author) {
        super(99, new JsonConfiguration().add("author", author));
    }
}
