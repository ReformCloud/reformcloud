/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
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
package systems.reformcloud.reformcloud2.permissions.packets;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.enums.EnumUtil;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.channel.listener.ChannelListener;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.network.packet.Packet;
import systems.reformcloud.reformcloud2.permissions.PermissionManagement;
import systems.reformcloud.reformcloud2.permissions.objects.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.packets.util.PermissionAction;

public class PacketGroupAction extends Packet {

  private PermissionGroup permissionGroup;
  private PermissionAction permissionAction;

  public PacketGroupAction() {
  }

  public PacketGroupAction(PermissionGroup permissionGroup, PermissionAction permissionAction) {
    this.permissionGroup = permissionGroup;
    this.permissionAction = permissionAction;
  }

  @Override
  public int getId() {
    return PacketHelper.PERMISSION_BUS + 1;
  }

  @Override
  public void handlePacketReceive(@NotNull ChannelListener reader, @NotNull NetworkChannel channel) {
    if (ExecutorAPI.getInstance().getType() != ExecutorType.API) {
      switch (this.permissionAction) {
        case CREATE: {
          PermissionManagement.getInstance().createPermissionGroup(this.permissionGroup);
          PermissionManagement.getInstance().handleInternalPermissionGroupCreate(this.permissionGroup);
          break;
        }

        case UPDATE: {
          PermissionManagement.getInstance().updateGroup(this.permissionGroup);
          PermissionManagement.getInstance().handleInternalPermissionGroupUpdate(this.permissionGroup);
          break;
        }

        case DELETE: {
          PermissionManagement.getInstance().deleteGroup(this.permissionGroup.getName());
          PermissionManagement.getInstance().handleInternalPermissionGroupDelete(this.permissionGroup);
          break;
        }

        default: {
          throw new IllegalStateException("Unhandled permission action: " + this.permissionAction);
        }
      }

      return;
    }

    switch (this.permissionAction) {
      case UPDATE: {
        PermissionManagement.getInstance().handleInternalPermissionGroupUpdate(this.permissionGroup);
        break;
      }

      case DELETE: {
        PermissionManagement.getInstance().handleInternalPermissionGroupDelete(this.permissionGroup);
        break;
      }

      case CREATE: {
        PermissionManagement.getInstance().handleInternalPermissionGroupCreate(this.permissionGroup);
        break;
      }

      default: {
        throw new IllegalStateException("Unhandled permission action: " + this.permissionAction);
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
    this.permissionAction = EnumUtil.findEnumFieldByIndex(PermissionAction.class, buffer.readInt()).orElse(null);
  }
}
