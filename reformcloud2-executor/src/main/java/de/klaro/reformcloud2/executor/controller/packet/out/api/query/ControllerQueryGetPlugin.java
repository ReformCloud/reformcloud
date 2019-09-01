package de.klaro.reformcloud2.executor.controller.packet.out.api.query;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ControllerQueryGetPlugin extends DefaultPacket {

    public ControllerQueryGetPlugin(String name) {
        super(98, new JsonConfiguration().add("name", name));
    }
}
