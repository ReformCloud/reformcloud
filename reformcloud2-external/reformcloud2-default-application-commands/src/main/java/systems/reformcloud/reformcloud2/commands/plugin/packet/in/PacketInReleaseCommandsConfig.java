package systems.reformcloud.reformcloud2.commands.plugin.packet.in;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.commands.config.CommandsConfig;
import systems.reformcloud.reformcloud2.commands.plugin.CommandConfigHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.function.Consumer;

public final class PacketInReleaseCommandsConfig extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.EXTERNAL_BUS + 4;
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        CommandsConfig commandsConfig = packet.content().get("config", new TypeToken<CommandsConfig>() {
        });
        if (commandsConfig == null || CommandConfigHandler.getInstance() == null) {
            return;
        }

        CommandConfigHandler.getInstance().handleCommandConfigRelease(commandsConfig);
    }
}
