package de.klaro.reformcloud2.executor.controller.packet.out.api;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

import java.util.List;

public final class ControllerAPIAction extends DefaultPacket {

    public ControllerAPIAction(APIAction action, List<Object> args) {
        super(
                46,
                new JsonConfiguration()
                    .add("action", action)
                    .add("args", args)
        );
    }

    public enum APIAction {

        SEND_MESSAGE,

        KICK_PLAYER,

        PLAY_SOUND,

        SEND_TITLE,

        PLAY_ENTITY_EFFECT,

        PLAY_EFFECT,

        RESPAWN,

        //ENTITY_TELEPORT, //For update?

        LOCATION_TELEPORT,

        CONNECT,

        SET_RESOURCE_PACK
    }
}
