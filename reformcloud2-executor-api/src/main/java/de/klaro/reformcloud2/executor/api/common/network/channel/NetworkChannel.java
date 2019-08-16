package de.klaro.reformcloud2.executor.api.common.network.channel;

import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.utility.name.ReNameable;

public interface NetworkChannel extends ReNameable {

    void sendPacket(Packet packet);

    void sendPacketSync(Packet packet);

    void sendPackets(Packet... packets);

    void sendPacketsSync(Packet... packets);

    boolean isConnected();

    void close();
}
