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
package systems.reformcloud.reformcloud2.node.protocol;

import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.network.packet.PacketProvider;
import systems.reformcloud.reformcloud2.protocol.api.NodeToApiRequestProcessInformationUpdateResult;
import systems.reformcloud.reformcloud2.protocol.node.*;
import systems.reformcloud.reformcloud2.protocol.shared.*;

public final class PacketRegister {

    private PacketRegister() {
        throw new UnsupportedOperationException();
    }

    public static void register() {
        PacketProvider packetProvider = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(PacketProvider.class);

        // node <-> node
        packetProvider.registerPacket(NodeToHeadNodeCreateProcess.class);
        packetProvider.registerPacket(NodeToNodeCopyProcessToTemplate.class);
        packetProvider.registerPacket(NodeToNodeCreateMainGroup.class);
        packetProvider.registerPacket(NodeToNodeCreateProcessGroup.class);
        packetProvider.registerPacket(NodeToNodeDeleteMainGroup.class);
        packetProvider.registerPacket(NodeToNodeDeleteProcessGroup.class);
        packetProvider.registerPacket(NodeToNodeDisconnectPlayer.class);
        packetProvider.registerPacket(NodeToNodeGetLastLogLines.class);
        packetProvider.registerPacket(NodeToNodePlayEffectToPlayer.class);
        packetProvider.registerPacket(NodeToNodePlaySoundToPlayer.class);
        packetProvider.registerPacket(NodeToNodeProcessCommand.class);
        packetProvider.registerPacket(NodeToNodeProcessScreenLines.class);
        packetProvider.registerPacket(NodeToNodePublishChannelMessage.class);
        packetProvider.registerPacket(NodeToNodeRegisterProcess.class);
        packetProvider.registerPacket(NodeToNodeRequestNodeInformationUpdate.class);
        packetProvider.registerPacket(NodeToNodeRequestProcessUpdate.class);
        packetProvider.registerPacket(NodeToNodeSendPlayerMessage.class);
        packetProvider.registerPacket(NodeToNodeSendPlayerTitle.class);
        packetProvider.registerPacket(NodeToNodeSendPlayerToServer.class);
        packetProvider.registerPacket(NodeToNodeSendProcessCommand.class);
        packetProvider.registerPacket(NodeToNodeSetMainGroups.class);
        packetProvider.registerPacket(NodeToNodeSetPlayerLocation.class);
        packetProvider.registerPacket(NodeToNodeSetProcesses.class);
        packetProvider.registerPacket(NodeToNodeSetProcessGroups.class);
        packetProvider.registerPacket(NodeToNodeSetProcessRuntimeState.class);
        packetProvider.registerPacket(NodeToNodeTabCompleteCommand.class);
        packetProvider.registerPacket(NodeToNodeToggleProcessScreen.class);
        packetProvider.registerPacket(NodeToNodeUnregisterProcess.class);
        packetProvider.registerPacket(NodeToNodeUpdateMainGroup.class);
        packetProvider.registerPacket(NodeToNodeUpdateNodeInformation.class);
        packetProvider.registerPacket(NodeToNodeUpdateProcess.class);
        packetProvider.registerPacket(NodeToNodeUpdateProcessGroup.class);
        packetProvider.registerPacket(NodeToNodeUploadProcessLog.class);

        // api -> node
        packetProvider.registerPacket(ApiToNodeClearDatabaseTable.class);
        packetProvider.registerPacket(ApiToNodeCompleteCommandLine.class);
        packetProvider.registerPacket(ApiToNodeConnectPlayerToPlayer.class);
        packetProvider.registerPacket(ApiToNodeCopyProcess.class);
        packetProvider.registerPacket(ApiToNodeCreateMainGroup.class);
        packetProvider.registerPacket(ApiToNodeCreateProcessGroup.class);
        packetProvider.registerPacket(ApiToNodeDeleteDatabaseTable.class);
        packetProvider.registerPacket(ApiToNodeDeleteMainGroup.class);
        packetProvider.registerPacket(ApiToNodeDeleteProcessGroup.class);
        packetProvider.registerPacket(ApiToNodeDispatchCommandLine.class);
        packetProvider.registerPacket(ApiToNodeGetAllTableEntries.class);
        packetProvider.registerPacket(ApiToNodeGetCurrentPlayerProcessUniqueIds.class);
        packetProvider.registerPacket(ApiToNodeGetDatabaseDocument.class);
        packetProvider.registerPacket(ApiToNodeGetDatabaseEntryCount.class);
        packetProvider.registerPacket(ApiToNodeGetDatabaseNames.class);
        packetProvider.registerPacket(ApiToNodeGetIngameMessages.class);
        packetProvider.registerPacket(ApiToNodeGetLastProcessLogLines.class);
        packetProvider.registerPacket(ApiToNodeGetMainGroup.class);
        packetProvider.registerPacket(ApiToNodeGetMainGroupCount.class);
        packetProvider.registerPacket(ApiToNodeGetMainGroupNames.class);
        packetProvider.registerPacket(ApiToNodeGetMainGroupObjects.class);
        packetProvider.registerPacket(ApiToNodeGetNodeInformationByName.class);
        packetProvider.registerPacket(ApiToNodeGetNodeInformationByUniqueId.class);
        packetProvider.registerPacket(ApiToNodeGetNodeNames.class);
        packetProvider.registerPacket(ApiToNodeGetNodeObjects.class);
        packetProvider.registerPacket(ApiToNodeGetNodeUniqueIds.class);
        packetProvider.registerPacket(ApiToNodeGetPlayerUniqueIdFromName.class);
        packetProvider.registerPacket(ApiToNodeGetProcessCount.class);
        packetProvider.registerPacket(ApiToNodeGetProcessCountByProcessGroup.class);
        packetProvider.registerPacket(ApiToNodeGetProcessGroup.class);
        packetProvider.registerPacket(ApiToNodeGetProcessGroupCount.class);
        packetProvider.registerPacket(ApiToNodeGetProcessGroupNames.class);
        packetProvider.registerPacket(ApiToNodeGetProcessGroupObjects.class);
        packetProvider.registerPacket(ApiToNodeGetProcessInformationByName.class);
        packetProvider.registerPacket(ApiToNodeGetProcessInformationByUniqueId.class);
        packetProvider.registerPacket(ApiToNodeGetProcessInformationObjects.class);
        packetProvider.registerPacket(ApiToNodeGetProcessInformationObjectsByMainGroup.class);
        packetProvider.registerPacket(ApiToNodeGetProcessInformationObjectsByProcessGroup.class);
        packetProvider.registerPacket(ApiToNodeGetProcessInformationObjectsByVersion.class);
        packetProvider.registerPacket(ApiToNodeGetProcessUniqueIds.class);
        packetProvider.registerPacket(ApiToNodeGetTableEntryNames.class);
        packetProvider.registerPacket(ApiToNodeHasTableDocument.class);
        packetProvider.registerPacket(ApiToNodeInsertDocumentIntoTable.class);
        packetProvider.registerPacket(ApiToNodeIsNodePresentByName.class);
        packetProvider.registerPacket(ApiToNodeIsNodePresentByUniqueId.class);
        packetProvider.registerPacket(ApiToNodeIsPlayerOnlineByName.class);
        packetProvider.registerPacket(ApiToNodeIsPlayerOnlineByUniqueId.class);
        packetProvider.registerPacket(ApiToNodePrepareProcess.class);
        packetProvider.registerPacket(ApiToNodePublishNodeChannelMessage.class);
        packetProvider.registerPacket(ApiToNodeRemoveDocumentFromTable.class);
        packetProvider.registerPacket(ApiToNodeRequestNodeInformationUpdate.class);
        packetProvider.registerPacket(ApiToNodeRequestProcessInformationUpdate.class);
        packetProvider.registerPacket(ApiToNodeSendChannelMessageToProcess.class);
        packetProvider.registerPacket(ApiToNodeSendChannelMessageToProcessGroup.class);
        packetProvider.registerPacket(ApiToNodeSendGlobalChannelMessage.class);
        packetProvider.registerPacket(ApiToNodeSendProcessCommand.class);
        packetProvider.registerPacket(ApiToNodeSetProcessRuntimeState.class);
        packetProvider.registerPacket(ApiToNodeUpdateDocumentInTable.class);
        packetProvider.registerPacket(ApiToNodeUpdateMainGroup.class);
        packetProvider.registerPacket(ApiToNodeUpdateProcessGroup.class);
        packetProvider.registerPacket(ApiToNodeUpdateProcessInformation.class);
        packetProvider.registerPacket(ApiToNodeUploadProcessLog.class);

        // node -> api
        packetProvider.registerPacket(NodeToApiRequestProcessInformationUpdateResult.class);

        // node <-> api & node <-> node (shared packets)
        packetProvider.registerPacket(PacketAuthBegin.class);
        packetProvider.registerPacket(PacketAuthSuccess.class);
        packetProvider.registerPacket(PacketChannelMessage.class);
        packetProvider.registerPacket(PacketConnectPlayerToServer.class);
        packetProvider.registerPacket(PacketDisconnectPlayer.class);
        packetProvider.registerPacket(PacketPlayEffectToPlayer.class);
        packetProvider.registerPacket(PacketPlaySoundToPlayer.class);
        packetProvider.registerPacket(PacketSendPlayerMessage.class);
        packetProvider.registerPacket(PacketSendPlayerTitle.class);
        packetProvider.registerPacket(PacketSetPlayerLocation.class);
    }
}
