package systems.reformcloud.reformcloud2.permissions.packets;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.packets.util.PermissionAction;
import systems.reformcloud.reformcloud2.permissions.util.basic.DefaultPermissionUtil;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;

public class PacketUserAction extends Packet {

    public PacketUserAction() {
    }

    public PacketUserAction(PermissionUser permissionUser, PermissionAction permissionAction) {
        this.permissionUser = permissionUser;
        this.permissionAction = permissionAction;
    }

    private PermissionUser permissionUser;

    private PermissionAction permissionAction;

    @Override
    public int getId() {
        return PacketHelper.PERMISSION_BUS + 4;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        switch (this.permissionAction) {
            case DELETE: {
                if (ExecutorAPI.getInstance().getType() != ExecutorType.API) {
                    PermissionAPI.getInstance().getPermissionUtil().deleteUser(permissionUser.getUniqueID());
                } else {
                    PermissionAPI.getInstance().getPermissionUtil().handleInternalUserCreate(permissionUser);
                }

                break;
            }

            case UPDATE: {
                if (ExecutorAPI.getInstance().getType() != ExecutorType.API) {
                    PermissionAPI.getInstance().getPermissionUtil().updateUser(permissionUser);
                } else {
                    PermissionAPI.getInstance().getPermissionUtil().handleInternalUserUpdate(permissionUser);
                }

                break;
            }

            case CREATE: {
                if (ExecutorAPI.getInstance().getType() != ExecutorType.API) {
                    ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().insert(
                            DefaultPermissionUtil.PERMISSION_PLAYER_TABLE,
                            permissionUser.getUniqueID().toString(),
                            null,
                            new JsonConfiguration().add("user", permissionUser)
                    );
                } else {
                    PermissionAPI.getInstance().getPermissionUtil().handleInternalUserCreate(permissionUser);
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
