package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

import java.util.Arrays;

public final class ExternalAPIPacketOutAPIAction extends DefaultPacket {

    public ExternalAPIPacketOutAPIAction(APIAction action, Object... args) {
        super(
                ExternalAPIImplementation.EXTERNAL_PACKET_ID + 26,
                new JsonConfiguration()
                        .add("action", action)
                        .add("args", Arrays.asList(args))
        );
    }

    public enum APIAction {

        SEND_MESSAGE,

        KICK_PLAYER,

        KICK_SERVER,

        PLAY_SOUND,

        SEND_TITLE,

        PLAY_ENTITY_EFFECT,

        PLAY_EFFECT,

        RESPAWN,

        //ENTITY_TELEPORT, //For update?

        LOCATION_TELEPORT,

        CONNECT,

        CONNECT_PLAYER,

        SET_RESOURCE_PACK
    }
}
