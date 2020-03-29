package systems.reformcloud.reformcloud2.executor.controller.network.packets.in;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ChannelMessageReceivedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ErrorReportHandling;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ReceiverType;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.Collection;
import java.util.function.Consumer;

public class ControllerPacketInHandleChannelMessage extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.MESSAGING_BUS + 1;
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        JsonConfiguration message = packet.content().get("message");
        String baseChannel = packet.content().getString("base");
        String subChannel = packet.content().getString("sub");

        if (packet.content().has("handling")) {
            Collection<String> receivers = packet.content().get("receivers", new TypeToken<Collection<String>>() {
            });
            ErrorReportHandling reportHandling = packet.content().get("handling", ErrorReportHandling.class);

            if (receivers == null || reportHandling == null) {
                return;
            }

            ExecutorAPI.getInstance().getSyncAPI().getMessageSyncAPI().sendChannelMessageSync(
                    message,
                    baseChannel,
                    subChannel,
                    reportHandling,
                    receivers.toArray(new String[0])
            );
        } else {
            Collection<ReceiverType> receivers = packet.content().get("receivers", new TypeToken<Collection<ReceiverType>>() {
            });
            if (receivers == null) {
                return;
            }

            ExecutorAPI.getInstance().getSyncAPI().getMessageSyncAPI().sendChannelMessageSync(
                    message,
                    baseChannel,
                    subChannel,
                    receivers.toArray(new ReceiverType[0])
            );
        }

        ExecutorAPI.getInstance().getEventManager().callEvent(new ChannelMessageReceivedEvent(message, baseChannel, subChannel));
    }
}
