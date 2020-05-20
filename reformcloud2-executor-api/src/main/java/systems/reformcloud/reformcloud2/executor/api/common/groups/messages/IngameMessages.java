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
package systems.reformcloud.reformcloud2.executor.api.common.groups.messages;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

import java.text.MessageFormat;

public final class IngameMessages implements SerializableObject {

    public static final TypeToken<IngameMessages> TYPE = new TypeToken<IngameMessages>() {
    };
    private String prefix = "§6§lR§e§leform§6§lC§e§lloud§6§l2 §7|";
    private String processRegistered = "%prefix% §7Registered process §e{0}";
    private String processStarted = "%prefix% §7The process §e{0} §7is §astarting §7now...";
    private String processConnected = "%prefix% §7The process §e{0} §7 is §aconnected to the network";
    private String processStopped = "%prefix% §7The process §e{0} §7is now §cstopped§7...";
    private String noHubServerAvailable = "%prefix% There is currently §cno §7Hub-Server available";
    private String alreadyConnectedToHub = "%prefix% You are §ealready connected to a §6Hub-Server";
    private String connectingToHub = "%prefix% §aSending §7to hub §6{0}§7...";
    private String currentProcessClosed = "%prefix% §7The current process got §cstopped";
    private String commandExecuteSuccess = "%prefix% §aSuccessfully §7proceeded!";
    private String processFullMessage = "§4§lThe process is full";
    private String processEnterPermissionNotSet = "§4§lYou do not have permission to join this process";
    private String processInMaintenanceMessage = "§4§lThis process is currently in maintenance";
    private String alreadyConnectedMessage = "§4§lYou are not allowed to join this process";
    private String processNotReadyToAcceptPlayersMessage = "§4§lThis process is not ready to accept connections";
    private String alreadyConnectedToNetwork = "§cYou are already on the network";

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
        this.alreadyConnectedMessage = buffer.readString();
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
