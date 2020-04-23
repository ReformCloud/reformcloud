package systems.reformcloud.reformcloud2.executor.api.network.packets.in;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.executor.PlayerAPIExecutor;

import java.util.UUID;
import java.util.function.Consumer;

import static systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out.ExternalAPIPacketOutAPIAction.APIAction;

public final class APIPacketInAPIAction implements Packet {

    public APIPacketInAPIAction(PlayerAPIExecutor executor) {
        this.executor = executor;
    }

    private final PlayerAPIExecutor executor;

    @Override
    public int getHandlingPacketID() {
        return 46;
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        APIAction apiAction = packet.content().get("action", APIAction.class);
        UUID targetPlayer = packet.content().get("1", UUID.class);

        switch (apiAction) {
            case CONNECT: {
                try {
                    UUID uuid = packet.content().get("2", UUID.class);
                    executor.executeConnect(targetPlayer, uuid);
                } catch (final Throwable throwable) {
                    executor.executeConnect(targetPlayer, packet.content().getString("2"));
                }
                break;
            }

            case RESPAWN: {
                executor.executeRespawn(targetPlayer);
                break;
            }

            case PLAY_SOUND: {
                executor.executePlaySound(targetPlayer,
                        packet.content().getString("2"),
                        packet.content().get("3", Float.class),
                        packet.content().get("4", Float.class)
                );
                break;
            }

            case SEND_TITLE: {
                executor.executeSendTitle(targetPlayer,
                        packet.content().getString("2"),
                        packet.content().getString("3"),
                        packet.content().getInteger("4"),
                        packet.content().getInteger("5"),
                        packet.content().getInteger("6")
                );
                break;
            }

            case KICK_PLAYER: {
                executor.executeKickPlayer(targetPlayer, packet.content().getString("2"));
                break;
            }

            case PLAY_EFFECT: {
                executor.executePlayEffect(targetPlayer,
                        packet.content().getString("2"),
                        packet.content().get("3", Object.class)
                );
                break;
            }

            case PLAY_ENTITY_EFFECT: {
                executor.executePlayEffect(targetPlayer, packet.content().getString("2"));
                break;
            }

            case SEND_MESSAGE: {
                executor.executeSendMessage(targetPlayer, packet.content().getString("2"));
                break;
            }

            case LOCATION_TELEPORT: {
                executor.executeTeleport(targetPlayer,
                        packet.content().getString("2"),
                        packet.content().get("3", Double.class),
                        packet.content().get("4", Double.class),
                        packet.content().get("5", Double.class),
                        packet.content().get("6", Float.class),
                        packet.content().get("7", Float.class)
                );
                break;
            }

            case SET_RESOURCE_PACK: {
                executor.executeSetResourcePack(targetPlayer, packet.content().getString("2"));
                break;
            }
        }
    }

    @Override
    public int getId() {
        return 42;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {

    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {

    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {

    }
}
