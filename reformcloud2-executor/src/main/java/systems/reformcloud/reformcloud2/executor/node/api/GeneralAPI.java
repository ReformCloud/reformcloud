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
package systems.reformcloud.reformcloud2.executor.node.api;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.AsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.SyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.console.ConsoleAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.console.ConsoleSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.group.GroupAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.group.GroupSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.MessageAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.MessageSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.player.PlayerAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.player.PlayerSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.process.ProcessSyncAPI;
import systems.reformcloud.reformcloud2.executor.node.api.console.ConsoleAPIImplementation;
import systems.reformcloud.reformcloud2.executor.node.api.database.DatabaseAPIImplementation;
import systems.reformcloud.reformcloud2.executor.node.api.group.GroupAPIImplementation;
import systems.reformcloud.reformcloud2.executor.node.api.message.ChannelMessageAPIImplementation;
import systems.reformcloud.reformcloud2.executor.node.api.player.PlayerAPIImplementation;
import systems.reformcloud.reformcloud2.executor.node.api.process.ProcessAPIImplementation;

public class GeneralAPI implements SyncAPI, AsyncAPI {

    private final ConsoleAPIImplementation consoleAPI;
    private final DatabaseAPIImplementation databaseAPI;
    private final GroupAPIImplementation groupAPI;
    private final PlayerAPIImplementation playerAPI;
    private final ProcessAPIImplementation processAPI;
    private final ChannelMessageAPIImplementation channelAPI;

    public GeneralAPI(
            ConsoleAPIImplementation consoleAPI,
            DatabaseAPIImplementation databaseAPI,
            GroupAPIImplementation groupAPI,
            PlayerAPIImplementation playerAPI,
            ProcessAPIImplementation processAPI,
            ChannelMessageAPIImplementation channelAPI
    ) {
        this.consoleAPI = consoleAPI;
        this.databaseAPI = databaseAPI;
        this.groupAPI = groupAPI;
        this.playerAPI = playerAPI;
        this.processAPI = processAPI;
        this.channelAPI = channelAPI;
    }

    @NotNull
    @Override
    public ProcessAsyncAPI getProcessAsyncAPI() {
        return this.processAPI;
    }

    @NotNull
    @Override
    public GroupAsyncAPI getGroupAsyncAPI() {
        return this.groupAPI;
    }

    @NotNull
    @Override
    public ConsoleAsyncAPI getConsoleAsyncAPI() {
        return this.consoleAPI;
    }

    @NotNull
    @Override
    public PlayerAsyncAPI getPlayerAsyncAPI() {
        return this.playerAPI;
    }

    @NotNull
    @Override
    public DatabaseAsyncAPI getDatabaseAsyncAPI() {
        return this.databaseAPI;
    }

    @NotNull
    @Override
    public MessageAsyncAPI getMessageAsyncAPI() {
        return this.channelAPI;
    }

    @NotNull
    @Override
    public ProcessSyncAPI getProcessSyncAPI() {
        return this.processAPI;
    }

    @NotNull
    @Override
    public GroupSyncAPI getGroupSyncAPI() {
        return this.groupAPI;
    }

    @NotNull
    @Override
    public ConsoleSyncAPI getConsoleSyncAPI() {
        return this.consoleAPI;
    }

    @NotNull
    @Override
    public PlayerSyncAPI getPlayerSyncAPI() {
        return this.playerAPI;
    }

    @NotNull
    @Override
    public DatabaseSyncAPI getDatabaseSyncAPI() {
        return this.databaseAPI;
    }

    @NotNull
    @Override
    public MessageSyncAPI getMessageSyncAPI() {
        return this.channelAPI;
    }
}
