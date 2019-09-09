package de.klaro.reformcloud2.executor.controller.packet.in.query;

import com.google.gson.reflect.TypeToken;
import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import de.klaro.reformcloud2.executor.api.common.groups.utils.Version;
import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;
import de.klaro.reformcloud2.executor.api.common.utility.list.Links;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public final class ControllerQueryInFilterLobbyForPlayer implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.CONTROLLER_QUERY_BUS + 1;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        List<String> perms = packet.content().get("perms", new TypeToken<List<String>>() {});
        Version version = packet.content().get("version", Version.class);
        responses.accept(new DefaultPacket(-1, new JsonConfiguration().add("result", filter(perms, version))));
    }

    private ProcessInformation filter(List<String> perms, Version version) {
        List<ProcessInformation> out = new ArrayList<>();
        ExecutorAPI.getInstance().getAllProcesses().forEach(new Consumer<ProcessInformation>() {
            @Override
            public void accept(ProcessInformation processInformation) {
                if (!processInformation.isLobby() || !processInformation.getNetworkInfo().isConnected()) {
                    return;
                }

                if (!version.equals(Version.WATERDOG_PE) && processInformation.getTemplate().getVersion().equals(Version.NUKKIT_X)) {
                    return;
                }

                final PlayerAccessConfiguration playerAccessConfiguration = processInformation.getProcessGroup().getPlayerAccessConfiguration();
                if (playerAccessConfiguration.isMaintenance() && perms.contains("reformcloud2.maintenance")) {
                    out.add(processInformation);
                    return;
                }

                if (playerAccessConfiguration.isUseCloudPlayerLimit()
                        && processInformation.getOnlineCount() < playerAccessConfiguration.getMaxPlayers()) {
                    out.add(processInformation);
                    return;
                }

                if (playerAccessConfiguration.isJoinOnlyPerPermission()
                        && playerAccessConfiguration.getJoinPermission() != null
                        && Links.toLowerCase(perms).contains(playerAccessConfiguration.getJoinPermission().toLowerCase())) {
                    out.add(processInformation);
                    return;
                }

                if (!playerAccessConfiguration.isMaintenance()
                        && !playerAccessConfiguration.isUseCloudPlayerLimit()
                        && !playerAccessConfiguration.isJoinOnlyPerPermission()) {
                    out.add(processInformation);
                }
            }
        });

        if (out.size() == 0) {
            return null;
        }

        return out.get(new Random().nextInt(out.size()));
    }
}
