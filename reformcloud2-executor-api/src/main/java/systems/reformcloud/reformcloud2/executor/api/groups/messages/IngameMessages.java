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
package systems.reformcloud.reformcloud2.executor.api.groups.messages;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;

import java.text.MessageFormat;

public final class IngameMessages implements SerializableObject {

    public static final TypeToken<IngameMessages> TYPE = new TypeToken<IngameMessages>() {
    };
    private String prefix = "§6Cloud §7|";
    private String processRegistered = "%prefix% §7Registered process §e{0}";
    private String processStarted = "%prefix% §7The process §e{0} §7is §astarting §7now...";
    private String processConnected = "%prefix% §7The process §e{0} §7is §aconnected to the network";
    private String processStopped = "%prefix% §7The process §e{0} §7is now §cstopping§7...";
    private String noHubServerAvailable = "%prefix% §7There is currently §cno §7hub server available";
    private String alreadyConnectedToHub = "%prefix% §7You are §calready §7connected to a hub server";
    private String connectingToHub = "%prefix% §7You will be connected to the §e{0} §7hub...";
    private String currentProcessClosed = "%prefix% §7The server you were on was removed and you got kicked";
    private String commandExecuteSuccess = "%prefix% §7The command was executed §asuccessfully";
    private String processFullMessage = "%prefix% §7You cannot enter this server because it is §cfull";
    private String processEnterPermissionNotSet = "%prefix% §7You cannot enter this server because you do not have sufficient permissions";
    private String processInMaintenanceMessage = "%prefix% §7You cannot enter this server because it is currently under maintenance and you do not have the necessary permissions to enter it during this time";
    private String alreadyConnectedMessage = "%prefix% §7You are already on this server! (If this is not so try it again immediately)";
    private String processNotReadyToAcceptPlayersMessage = "%prefix% §7This server is not yet ready to allow players to connect";
    private String alreadyConnectedToNetwork = "%prefix% §cYou are already on the network!";

    public IngameMessages() {
    }

    public IngameMessages(String prefix, String processRegistered, String processStarted, String processConnected, String processStopped, String noHubServerAvailable,
                          String alreadyConnectedToHub, String connectingToHub, String currentProcessClosed, String commandExecuteSuccess, String processFullMessage,
                          String processEnterPermissionNotSet, String processInMaintenanceMessage, String alreadyConnectedMessage, String processNotReadyToAcceptPlayersMessage, String alreadyConnectedToNetwork) {
        this.prefix = prefix;
        this.processRegistered = processRegistered;
        this.processStarted = processStarted;
        this.processConnected = processConnected;
        this.processStopped = processStopped;
        this.noHubServerAvailable = noHubServerAvailable;
        this.alreadyConnectedToHub = alreadyConnectedToHub;
        this.connectingToHub = connectingToHub;
        this.currentProcessClosed = currentProcessClosed;
        this.commandExecuteSuccess = commandExecuteSuccess;
        this.processFullMessage = processFullMessage;
        this.processEnterPermissionNotSet = processEnterPermissionNotSet;
        this.processInMaintenanceMessage = processInMaintenanceMessage;
        this.alreadyConnectedMessage = alreadyConnectedMessage;
        this.processNotReadyToAcceptPlayersMessage = processNotReadyToAcceptPlayersMessage;
        this.alreadyConnectedToNetwork = alreadyConnectedToNetwork;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getProcessRegistered() {
        return this.processRegistered;
    }

    public String getProcessStarted() {
        return this.processStarted;
    }

    public String getProcessConnected() {
        return this.processConnected;
    }

    public String getProcessStopped() {
        return this.processStopped;
    }

    public String getNoHubServerAvailable() {
        return this.noHubServerAvailable;
    }

    public String getAlreadyConnectedToHub() {
        return this.alreadyConnectedToHub;
    }

    public String getConnectingToHub() {
        return this.connectingToHub;
    }

    public String getCurrentProcessClosed() {
        return this.currentProcessClosed;
    }

    public String getProcessFullMessage() {
        return this.processFullMessage;
    }

    public String getProcessEnterPermissionNotSet() {
        return this.processEnterPermissionNotSet;
    }

    public String getProcessInMaintenanceMessage() {
        return this.processInMaintenanceMessage;
    }

    public String getAlreadyConnectedMessage() {
        return this.alreadyConnectedMessage;
    }

    public String getCommandExecuteSuccess() {
        return this.commandExecuteSuccess;
    }

    public String getProcessNotReadyToAcceptPlayersMessage() {
        return this.processNotReadyToAcceptPlayersMessage;
    }

    public String getAlreadyConnectedToNetwork() {
        return this.alreadyConnectedToNetwork;
    }

    @NotNull
    public String format(@NotNull String message, @NotNull Object... replacements) {
        message = message.replace("%prefix%", this.prefix).replace("&", "§");
        return MessageFormat.format(message, replacements);
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.prefix);
        buffer.writeString(this.processRegistered);
        buffer.writeString(this.processStarted);
        buffer.writeString(this.processConnected);
        buffer.writeString(this.processStopped);
        buffer.writeString(this.noHubServerAvailable);
        buffer.writeString(this.alreadyConnectedToHub);
        buffer.writeString(this.connectingToHub);
        buffer.writeString(this.currentProcessClosed);
        buffer.writeString(this.commandExecuteSuccess);
        buffer.writeString(this.processFullMessage);
        buffer.writeString(this.processEnterPermissionNotSet);
        buffer.writeString(this.processInMaintenanceMessage);
        buffer.writeString(this.alreadyConnectedMessage);
        buffer.writeString(this.processNotReadyToAcceptPlayersMessage);
        buffer.writeString(this.alreadyConnectedToNetwork);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.prefix = buffer.readString();
        this.processRegistered = buffer.readString();
        this.processStarted = buffer.readString();
        this.processConnected = buffer.readString();
        this.processStopped = buffer.readString();
        this.noHubServerAvailable = buffer.readString();
        this.alreadyConnectedToHub = buffer.readString();
        this.connectingToHub = buffer.readString();
        this.currentProcessClosed = buffer.readString();
        this.commandExecuteSuccess = buffer.readString();
        this.processFullMessage = buffer.readString();
        this.processEnterPermissionNotSet = buffer.readString();
        this.processInMaintenanceMessage = buffer.readString();
        this.alreadyConnectedMessage = buffer.readString();
        this.processNotReadyToAcceptPlayersMessage = buffer.readString();
        this.alreadyConnectedToNetwork = buffer.readString();
    }
}
