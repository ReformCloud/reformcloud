/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.refomcloud.reformcloud2.embedded.network;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.network.packet.PacketProvider;
import systems.reformcloud.reformcloud2.protocol.api.*;
import systems.reformcloud.reformcloud2.protocol.node.*;
import systems.reformcloud.reformcloud2.protocol.shared.*;

class PacketRegister {

    private PacketRegister() {
        throw new AssertionError("You should not instantiate this class");
    }

    static void preAuth() {
        getPacketProvider().registerPacket(PacketAuthSuccess.class);
    }

    static void postAuth() {
        PacketProvider packetProvider = getPacketProvider();

        // unregister auth packet
        packetProvider.unregisterPacket(NetworkUtil.AUTH_BUS_END);

        // api -> node query result packets
        packetProvider.registerPacket(ApiToNodeGetAllTableEntriesResult.class);
        packetProvider.registerPacket(ApiToNodeGetCurrentPlayerProcessUniqueIdsResult.class);
        packetProvider.registerPacket(ApiToNodeGetDatabaseDocumentResult.class);
        packetProvider.registerPacket(ApiToNodeGetDatabaseEntryCountResult.class);
        packetProvider.registerPacket(ApiToNodeGetDatabaseNamesResult.class);
        packetProvider.registerPacket(ApiToNodeGetIngameMessagesResult.class);
        packetProvider.registerPacket(ApiToNodeGetNodeInformationResult.class);
        packetProvider.registerPacket(ApiToNodeGetNodeNamesResult.class);
        packetProvider.registerPacket(ApiToNodeGetNodeObjectsResult.class);
        packetProvider.registerPacket(ApiToNodeGetNodeUniqueIdsResult.class);
        packetProvider.registerPacket(ApiToNodeGetPlayerUniqueIdFromNameResult.class);
        packetProvider.registerPacket(ApiToNodeGetStringCollectionResult.class);
        packetProvider.registerPacket(ApiToNodeGetTableEntryNamesResult.class);
        packetProvider.registerPacket(ApiToNodeHasTableDocumentResult.class);
        packetProvider.registerPacket(ApiToNodeIsNodePresentResult.class);
        packetProvider.registerPacket(ApiToNodeIsPlayerOnlineResult.class);

        // node -> api
        packetProvider.registerPacket(NodeToApiMainGroupCreate.class);
        packetProvider.registerPacket(NodeToApiMainGroupDelete.class);
        packetProvider.registerPacket(NodeToApiMainGroupUpdated.class);
        packetProvider.registerPacket(NodeToApiProcessGroupCreate.class);
        packetProvider.registerPacket(NodeToApiProcessGroupDelete.class);
        packetProvider.registerPacket(NodeToApiProcessGroupUpdated.class);
        packetProvider.registerPacket(NodeToApiProcessRegister.class);
        packetProvider.registerPacket(NodeToApiProcessUnregister.class);
        packetProvider.registerPacket(NodeToApiProcessUpdated.class);
        packetProvider.registerPacket(NodeToApiRequestProcessInformationUpdate.class);

        // node <-> api (shared packets)
        packetProvider.registerPacket(PacketChannelMessage.class);
        packetProvider.registerPacket(PacketConnectPlayerToServer.class);
        packetProvider.registerPacket(PacketDisconnectPlayer.class);
        packetProvider.registerPacket(PacketPlayEffectToPlayer.class);
        packetProvider.registerPacket(PacketPlaySoundToPlayer.class);
        packetProvider.registerPacket(PacketSendPlayerMessage.class);
        packetProvider.registerPacket(PacketSendPlayerTitle.class);
        packetProvider.registerPacket(PacketSetPlayerLocation.class);
    }

    @NotNull
    private static PacketProvider getPacketProvider() {
        return ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(PacketProvider.class);
    }
}
