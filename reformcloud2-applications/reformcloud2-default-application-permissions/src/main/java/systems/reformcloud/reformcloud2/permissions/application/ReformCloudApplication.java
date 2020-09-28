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
package systems.reformcloud.reformcloud2.permissions.application;

import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.application.updater.ApplicationUpdateRepository;
import systems.reformcloud.reformcloud2.executor.api.command.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.event.EventManager;
import systems.reformcloud.reformcloud2.executor.api.network.packet.PacketProvider;
import systems.reformcloud.reformcloud2.permissions.PermissionManagement;
import systems.reformcloud.reformcloud2.permissions.application.command.CommandPerms;
import systems.reformcloud.reformcloud2.permissions.application.listener.ProcessInclusionHandler;
import systems.reformcloud.reformcloud2.permissions.application.updater.PermissionsAddonUpdater;
import systems.reformcloud.reformcloud2.permissions.packets.PacketHelper;

public class ReformCloudApplication extends Application {

    private static final ApplicationUpdateRepository REPOSITORY = new PermissionsAddonUpdater();

    private static ReformCloudApplication instance;

    public static ReformCloudApplication getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).registerListener(new ProcessInclusionHandler());
    }

    @Override
    public void onEnable() {
        PermissionManagement.setup();
        PacketHelper.addPacketHandler();
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(CommandManager.class).registerCommand(
                new CommandPerms(),
                "Manages the permission users and permission groups in the database and on all currently running processes",
                "permissions", "cloudperms", "perms");
    }

    @Override
    public void onDisable() {
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(PacketProvider.class).unregisterPacket(PacketHelper.PERMISSION_BUS + 1);
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(PacketProvider.class).unregisterPacket(PacketHelper.PERMISSION_BUS + 4);
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(CommandManager.class).unregisterCommand("permissions");
    }

    @Nullable
    @Override
    public ApplicationUpdateRepository getUpdateRepository() {
        return REPOSITORY;
    }
}
