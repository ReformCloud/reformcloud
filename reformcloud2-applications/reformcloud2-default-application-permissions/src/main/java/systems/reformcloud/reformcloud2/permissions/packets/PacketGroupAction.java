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
package systems.reformcloud.reformcloud2.permissions.packets;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.permissions.PermissionManagement;
import systems.reformcloud.reformcloud2.permissions.objects.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.packets.util.PermissionAction;

public class PacketGroupAction extends Packet {

    public PacketGroupAction() {
    }

    public PacketGroupAction(PermissionGroup permissionGroup, PermissionAction permissionAction) {
        this.permissionGroup = permissionGroup;
        this.permissionAction = permissionAction;
    }

    private PermissionGroup permissionGroup;

    private PermissionAction permissionAction;

    @Override
    public int getId() {
        return PacketHelper.PERMISSION_BUS + 1;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        if (ExecutorAPI.getInstance().getType() != ExecutorType.API) {
            switch (this.permissionAction) {
                case CREATE: {
                    PermissionManagement.getInstance().createPermissionGroup(permissionGroup);
                    PermissionManagement.getInstance().handleInternalPermissionGroupCreate(permissionGroup);
                    break;
                }

                case UPDATE: {
                    PermissionManagement.getInstance().updateGroup(permissionGroup);
                    PermissionManagement.getInstance().handleInternalPermissionGroupUpdate(permissionGroup);
                    break;
                }

                case DELETE: {
                    PermissionManagement.getInstance().deleteGroup(permissionGroup.getName());
                    PermissionManagement.getInstance().handleInternalPermissionGroupDelete(permissionGroup);
                    break;
                }
            }

            return;
        }

        switch (permissionAction) {
            case UPDATE: {
                PermissionManagement.getInstance().handleInternalPermissionGroupUpdate(permissionGroup);
                break;
            }

            case DELETE: {
                PermissionManagement.getInstance().handleInternalPermissionGroupDelete(permissionGroup);
                break;
            }

            case CREATE: {
                PermissionManagement.getInstance().handleInternalPermissionGroupCreate(permissionGroup);
                break;
            }
        }
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObject(this.permissionGroup);
        buffer.writeInt(this.permissionAction.ordinal());
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.permissionGroup = buffer.readObject(PermissionGroup.class);
        this.permissionAction = PermissionAction.values()[buffer.readInt()];
    }
}
