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
package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;

import java.util.UUID;

public class PacketAPIProcessCopyByUniqueID extends PacketAPIProcessCopy {

    private UUID processUniqueID;

    public PacketAPIProcessCopyByUniqueID() {
        super(null, null, null);
    }

    public PacketAPIProcessCopyByUniqueID(UUID processUniqueID, String targetTemplate, String targetTemplateStorage, String targetTemplateGroup) {
        super(targetTemplate, targetTemplateStorage, targetTemplateGroup);
        this.processUniqueID = processUniqueID;
    }

    @Override
    public int getId() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 52;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        if (super.targetTemplate != null && super.targetTemplateGroup != null && super.targetTemplateStorage != null) {
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().copyProcess(
                    this.processUniqueID,
                    super.targetTemplate,
                    super.targetTemplateStorage,
                    super.targetTemplateGroup
            );
        } else if (super.targetTemplate != null && super.targetTemplateStorage != null) {
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().copyProcess(this.processUniqueID, super.targetTemplate, super.targetTemplateStorage);
        } else if (super.targetTemplate != null) {
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().copyProcess(this.processUniqueID, super.targetTemplate);
        } else {
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().copyProcess(this.processUniqueID);
        }
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        super.write(buffer);
        buffer.writeUniqueId(this.processUniqueID);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        super.read(buffer);
        this.processUniqueID = buffer.readUniqueId();
    }
}
