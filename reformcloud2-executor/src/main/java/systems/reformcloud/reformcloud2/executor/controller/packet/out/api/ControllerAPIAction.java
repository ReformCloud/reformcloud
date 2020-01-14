package systems.reformcloud.reformcloud2.executor.controller.packet.out.api;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class ControllerAPIAction extends JsonPacket {

    public ControllerAPIAction(APIAction action, List<Object> args) {
        super(46, null);

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
