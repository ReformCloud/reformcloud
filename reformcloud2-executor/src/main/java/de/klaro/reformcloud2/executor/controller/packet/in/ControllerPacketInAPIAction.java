package de.klaro.reformcloud2.executor.controller.packet.in;

import com.google.gson.reflect.TypeToken;
import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.api.basic.packets.out.ExternalAPIPacketOutAPIAction;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public final class ControllerPacketInAPIAction implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 26;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        List<Object> args = packet.content().get("args", new TypeToken<List<Object>>() {});
        UUID targetPlayer = (UUID) args.get(0);

        switch (packet.content().get("action", ExternalAPIPacketOutAPIAction.APIAction.class)) {
            case CONNECT: {
                ExecutorAPI.getInstance().connect(targetPlayer, (String) args.get(1));
                break;
            }

            case CONNECT_PLAYER: {
                ExecutorAPI.getInstance().connect(targetPlayer, (UUID) args.get(1));
                break;
            }

            case RESPAWN: {
                ExecutorAPI.getInstance().respawn(targetPlayer);
                break;
            }

            case PLAY_SOUND: {
                ExecutorAPI.getInstance().playSound(targetPlayer, (String) args.get(1), (Float) args.get(2), (Float) args.get(3));
                break;
            }

            case SEND_TITLE: {
                ExecutorAPI.getInstance().sendTitle(targetPlayer, (String) args.get(1), (String) args.get(2), (Integer) args.get(3), (Integer) args.get(4), (Integer) args.get(5));
                break;
            }

            case KICK_PLAYER: {
                ExecutorAPI.getInstance().kickPlayer(targetPlayer, (String) args.get(1));
                break;
            }

            case KICK_SERVER: {
                ExecutorAPI.getInstance().kickPlayerFromServer(targetPlayer, (String) args.get(1));
                break;
            }

            case PLAY_EFFECT: {
                ExecutorAPI.getInstance().playEffect(targetPlayer, (String) args.get(1), args.get(2));
                break;
            }

            case PLAY_ENTITY_EFFECT: {
                ExecutorAPI.getInstance().playEffect(targetPlayer, (String) args.get(1));
                break;
            }

            case SEND_MESSAGE: {
                ExecutorAPI.getInstance().sendMessage(targetPlayer, (String) args.get(1));
                break;
            }

            case LOCATION_TELEPORT: {
                ExecutorAPI.getInstance().teleport(targetPlayer, (String) args.get(1), (Double) args.get(2), (Double) args.get(3), (Double) args.get(4), (Float) args.get(5), (Float) args.get(6));
                break;
            }

            case SET_RESOURCE_PACK: {
                ExecutorAPI.getInstance().setResourcePack(targetPlayer, (String) args.get(1));
                break;
            }
        }
    }
}
