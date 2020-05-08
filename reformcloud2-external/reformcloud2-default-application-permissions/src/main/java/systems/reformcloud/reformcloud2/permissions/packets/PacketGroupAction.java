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
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.packets.util.PermissionAction;
import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;

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
                    PermissionAPI.getInstance().getPermissionUtil().createGroup(permissionGroup.getName());
                    PermissionAPI.getInstance().getPermissionUtil().handleInternalPermissionGroupCreate(permissionGroup);
                    break;
                }

                case UPDATE: {
                    PermissionAPI.getInstance().getPermissionUtil().updateGroup(permissionGroup);
                    PermissionAPI.getInstance().getPermissionUtil().handleInternalPermissionGroupUpdate(permissionGroup);
                    break;
                }

                case DELETE: {
                    PermissionAPI.getInstance().getPermissionUtil().deleteGroup(permissionGroup.getName());
                    PermissionAPI.getInstance().getPermissionUtil().handleInternalPermissionGroupDelete(permissionGroup);
                    break;
                }

                case DEFAULT_GROUPS_CHANGED: {
                    PermissionAPI.getInstance().getPermissionUtil().removeDefaultGroup(permissionGroup.getName());
                    PermissionAPI.getInstance().getPermissionUtil().handleInternalDefaultGroupsUpdate();
                    break;
                }
            }

            return;
        }

        switch (permissionAction) {
            case UPDATE: {
                PermissionAPI.getInstance().getPermissionUtil().handleInternalPermissionGroupUpdate(permissionGroup);
                break;
            }

            case DELETE: {
                PermissionAPI.getInstance().getPermissionUtil().handleInternalPermissionGroupDelete(permissionGroup);
                break;
            }

            case CREATE: {
                PermissionAPI.getInstance().getPermissionUtil().handleInternalPermissionGroupCreate(permissionGroup);
                break;
            }

            case DEFAULT_GROUPS_CHANGED: {
                PermissionAPI.getInstance().getPermissionUtil().handleInternalDefaultGroupsUpdate();
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
