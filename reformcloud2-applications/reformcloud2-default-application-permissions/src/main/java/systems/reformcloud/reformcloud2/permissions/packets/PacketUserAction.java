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
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.permissions.PermissionManagement;
import systems.reformcloud.reformcloud2.permissions.objects.user.PermissionUser;
import systems.reformcloud.reformcloud2.permissions.packets.util.PermissionAction;

public class PacketUserAction extends Packet {

    private PermissionUser permissionUser;
    private PermissionAction permissionAction;

    public PacketUserAction() {
    }

    public PacketUserAction(PermissionUser permissionUser, PermissionAction permissionAction) {
        this.permissionUser = permissionUser;
        this.permissionAction = permissionAction;
    }

    @Override
    public int getId() {
        return PacketHelper.PERMISSION_BUS + 4;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        switch (this.permissionAction) {
            case DELETE: {
                PermissionManagement.getInstance().handleInternalUserDelete(permissionUser);
                if (ExecutorAPI.getInstance().getType() != ExecutorType.API) {
                    DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new PacketUserAction(permissionUser, PermissionAction.DELETE)));
                }

                break;
            }

            case UPDATE: {
                PermissionManagement.getInstance().handleInternalUserUpdate(permissionUser);
                if (ExecutorAPI.getInstance().getType() != ExecutorType.API) {
                    DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new PacketUserAction(permissionUser, PermissionAction.UPDATE)));
                }

                break;
            }

            case CREATE: {
                PermissionManagement.getInstance().handleInternalUserCreate(permissionUser);
                if (ExecutorAPI.getInstance().getType() != ExecutorType.API) {
                    DefaultChannelManager.INSTANCE.getAllSender().forEach(e -> e.sendPacket(new PacketUserAction(permissionUser, PermissionAction.CREATE)));
                }

                break;
            }
        }
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObject(this.permissionUser);
        buffer.writeInt(this.permissionAction.ordinal());
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.permissionUser = buffer.readObject(PermissionUser.class);
        this.permissionAction = PermissionAction.values()[buffer.readInt()];
    }
}
