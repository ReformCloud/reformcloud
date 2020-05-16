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
package systems.reformcloud.reformcloud2.permissions.application;

import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.ExecutorType;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.common.application.updater.ApplicationUpdateRepository;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
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
    public void onInstallable() {
        ExecutorAPI.getInstance().getEventManager().registerListener(new ProcessInclusionHandler());
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        PermissionManagement.setup();
        PacketHelper.addPacketHandler();
        this.getCommandManager().register(new CommandPerms());
    }

    @Nullable
    @Override
    public ApplicationUpdateRepository getUpdateRepository() {
        return REPOSITORY;
    }

    private CommandManager getCommandManager() {
        return ExecutorAPI.getInstance().getType().equals(ExecutorType.CONTROLLER)
                ? ControllerExecutor.getInstance().getCommandManager()
                : NodeExecutor.getInstance().getCommandManager();
    }
}
