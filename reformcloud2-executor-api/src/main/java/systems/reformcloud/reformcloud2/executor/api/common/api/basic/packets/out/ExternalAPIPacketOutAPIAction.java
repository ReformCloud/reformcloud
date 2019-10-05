package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

import java.util.concurrent.atomic.AtomicInteger;

public final class ExternalAPIPacketOutAPIAction extends DefaultPacket {

    public ExternalAPIPacketOutAPIAction(APIAction action, Object... args) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 26,null);

        JsonConfiguration jsonConfiguration = new JsonConfiguration().add("action", action);
        AtomicInteger atomicInteger = new AtomicInteger(1);
        for (Object o : args) {
            jsonConfiguration.add(Integer.toString(atomicInteger.getAndIncrement()), o);
        }
        setContent(jsonConfiguration);
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
