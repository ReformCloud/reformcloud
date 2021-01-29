/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.refomcloud.embedded.network;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.network.PacketIds;
import systems.reformcloud.network.packet.PacketProvider;
import systems.reformcloud.protocol.api.NodeToApiMainGroupCreate;
import systems.reformcloud.protocol.api.NodeToApiMainGroupDelete;
import systems.reformcloud.protocol.api.NodeToApiMainGroupUpdated;
import systems.reformcloud.protocol.api.NodeToApiProcessGroupCreate;
import systems.reformcloud.protocol.api.NodeToApiProcessGroupDelete;
import systems.reformcloud.protocol.api.NodeToApiProcessGroupUpdated;
import systems.reformcloud.protocol.api.NodeToApiProcessRegister;
import systems.reformcloud.protocol.api.NodeToApiProcessUnregister;
import systems.reformcloud.protocol.api.NodeToApiProcessUpdated;
import systems.reformcloud.protocol.api.NodeToApiRequestProcessInformationUpdate;
import systems.reformcloud.protocol.node.ApiToNodeCreateMainGroupResult;
import systems.reformcloud.protocol.node.ApiToNodeCreateProcessGroupResult;
import systems.reformcloud.protocol.node.ApiToNodeGetAllTableEntriesResult;
import systems.reformcloud.protocol.node.ApiToNodeGetCurrentPlayerProcessUniqueIdsResult;
import systems.reformcloud.protocol.node.ApiToNodeGetDatabaseDocumentResult;
import systems.reformcloud.protocol.node.ApiToNodeGetDatabaseEntryCountResult;
import systems.reformcloud.protocol.node.ApiToNodeGetDatabaseNamesResult;
import systems.reformcloud.protocol.node.ApiToNodeGetIngameMessagesResult;
import systems.reformcloud.protocol.node.ApiToNodeGetMainGroupCountResult;
import systems.reformcloud.protocol.node.ApiToNodeGetMainGroupObjectsResult;
import systems.reformcloud.protocol.node.ApiToNodeGetMainGroupResult;
import systems.reformcloud.protocol.node.ApiToNodeGetNodeInformationResult;
import systems.reformcloud.protocol.node.ApiToNodeGetNodeNamesResult;
import systems.reformcloud.protocol.node.ApiToNodeGetNodeObjectsResult;
import systems.reformcloud.protocol.node.ApiToNodeGetNodeUniqueIdsResult;
import systems.reformcloud.protocol.node.ApiToNodeGetPlayerUniqueIdFromNameResult;
import systems.reformcloud.protocol.node.ApiToNodeGetProcessCountResult;
import systems.reformcloud.protocol.node.ApiToNodeGetProcessGroupCountResult;
import systems.reformcloud.protocol.node.ApiToNodeGetProcessGroupObjectsResult;
import systems.reformcloud.protocol.node.ApiToNodeGetProcessGroupResult;
import systems.reformcloud.protocol.node.ApiToNodeGetProcessInformationObjectsResult;
import systems.reformcloud.protocol.node.ApiToNodeGetProcessInformationResult;
import systems.reformcloud.protocol.node.ApiToNodeGetProcessUniqueIdsResult;
import systems.reformcloud.protocol.node.ApiToNodeGetStringCollectionResult;
import systems.reformcloud.protocol.node.ApiToNodeGetTableEntryNamesResult;
import systems.reformcloud.protocol.node.ApiToNodeHasTableDocumentResult;
import systems.reformcloud.protocol.node.ApiToNodeIsNodePresentResult;
import systems.reformcloud.protocol.node.ApiToNodeIsPlayerOnlineResult;
import systems.reformcloud.protocol.node.ApiToNodeUploadProcessLogResult;
import systems.reformcloud.protocol.shared.PacketAuthSuccess;
import systems.reformcloud.protocol.shared.PacketChannelMessage;
import systems.reformcloud.protocol.shared.PacketConnectPlayerToServer;
import systems.reformcloud.protocol.shared.PacketDisconnectPlayer;
import systems.reformcloud.protocol.shared.PacketPlayEffectToPlayer;
import systems.reformcloud.protocol.shared.PacketPlaySoundToPlayer;
import systems.reformcloud.protocol.shared.PacketSendActionBar;
import systems.reformcloud.protocol.shared.PacketSendBossBar;
import systems.reformcloud.protocol.shared.PacketSendPlayerMessage;
import systems.reformcloud.protocol.shared.PacketSendPlayerTitle;
import systems.reformcloud.protocol.shared.PacketSetPlayerLocation;

final class PacketRegister {

  private PacketRegister() {
    throw new AssertionError("You should not instantiate this class");
  }

  static void preAuth() {
    getPacketProvider().registerPacket(PacketAuthSuccess.class);
  }

  static void postAuth() {
    PacketProvider packetProvider = getPacketProvider();

    // unregister auth packet
    packetProvider.unregisterPacket(PacketIds.AUTH_BUS + 1);

    // api -> node query result packets
    packetProvider.registerPacket(ApiToNodeCreateMainGroupResult.class);
    packetProvider.registerPacket(ApiToNodeCreateProcessGroupResult.class);
    packetProvider.registerPacket(ApiToNodeGetAllTableEntriesResult.class);
    packetProvider.registerPacket(ApiToNodeGetCurrentPlayerProcessUniqueIdsResult.class);
    packetProvider.registerPacket(ApiToNodeGetDatabaseDocumentResult.class);
    packetProvider.registerPacket(ApiToNodeGetDatabaseEntryCountResult.class);
    packetProvider.registerPacket(ApiToNodeGetDatabaseNamesResult.class);
    packetProvider.registerPacket(ApiToNodeGetIngameMessagesResult.class);
    packetProvider.registerPacket(ApiToNodeGetMainGroupCountResult.class);
    packetProvider.registerPacket(ApiToNodeGetMainGroupObjectsResult.class);
    packetProvider.registerPacket(ApiToNodeGetMainGroupResult.class);
    packetProvider.registerPacket(ApiToNodeGetNodeInformationResult.class);
    packetProvider.registerPacket(ApiToNodeGetNodeNamesResult.class);
    packetProvider.registerPacket(ApiToNodeGetNodeObjectsResult.class);
    packetProvider.registerPacket(ApiToNodeGetNodeUniqueIdsResult.class);
    packetProvider.registerPacket(ApiToNodeGetPlayerUniqueIdFromNameResult.class);
    packetProvider.registerPacket(ApiToNodeGetProcessCountResult.class);
    packetProvider.registerPacket(ApiToNodeGetProcessGroupCountResult.class);
    packetProvider.registerPacket(ApiToNodeGetProcessGroupObjectsResult.class);
    packetProvider.registerPacket(ApiToNodeGetProcessGroupResult.class);
    packetProvider.registerPacket(ApiToNodeGetProcessInformationObjectsResult.class);
    packetProvider.registerPacket(ApiToNodeGetProcessInformationResult.class);
    packetProvider.registerPacket(ApiToNodeGetProcessUniqueIdsResult.class);
    packetProvider.registerPacket(ApiToNodeGetStringCollectionResult.class);
    packetProvider.registerPacket(ApiToNodeGetTableEntryNamesResult.class);
    packetProvider.registerPacket(ApiToNodeHasTableDocumentResult.class);
    packetProvider.registerPacket(ApiToNodeIsNodePresentResult.class);
    packetProvider.registerPacket(ApiToNodeIsPlayerOnlineResult.class);
    packetProvider.registerPacket(ApiToNodeUploadProcessLogResult.class);

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
    packetProvider.registerPacket(PacketSendActionBar.class);
    packetProvider.registerPacket(PacketSendBossBar.class);
  }

  @NotNull
  private static PacketProvider getPacketProvider() {
    return ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(PacketProvider.class);
  }
}
