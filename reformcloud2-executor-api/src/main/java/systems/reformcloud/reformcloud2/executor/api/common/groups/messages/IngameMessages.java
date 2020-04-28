package systems.reformcloud.reformcloud2.executor.api.common.groups.messages;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

import java.text.MessageFormat;

public final class IngameMessages implements SerializableObject {

    public static final TypeToken<IngameMessages> TYPE = new TypeToken<IngameMessages>() {
    };

    public IngameMessages() {
    }

    public IngameMessages(String prefix, String processRegistered, String processStarted, String processConnected,
                          String processStopped, String noHubServerAvailable, String alreadyConnectedToHub,
                          String connectingToHub, String currentProcessClosed, String commandExecuteSuccess,
                          String processFullMessage, String processEnterPermissionNotSet, String processInMaintenanceMessage,
                          String alreadyConnectedMessage, String notUsingInternalProxy) {
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
        this.notUsingInternalProxy = notUsingInternalProxy;
    }

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

    private String notUsingInternalProxy = "§4§lTo connect to this server please use an internal proxy server";

    public String getPrefix() {
        return prefix;
    }

    public String getProcessRegistered() {
        return processRegistered;
    }

    public String getProcessStarted() {
        return processStarted;
    }

    public String getProcessConnected() {
        return processConnected;
    }

    public String getProcessStopped() {
        return processStopped;
    }

    public String getNoHubServerAvailable() {
        return noHubServerAvailable;
    }

    public String getAlreadyConnectedToHub() {
        return alreadyConnectedToHub;
    }

    public String getConnectingToHub() {
        return connectingToHub;
    }

    public String getCurrentProcessClosed() {
        return currentProcessClosed;
    }

    public String getProcessFullMessage() {
        return processFullMessage;
    }

    public String getProcessEnterPermissionNotSet() {
        return processEnterPermissionNotSet;
    }

    public String getProcessInMaintenanceMessage() {
        return processInMaintenanceMessage;
    }

    public String getAlreadyConnectedMessage() {
        return alreadyConnectedMessage;
    }

    public String getCommandExecuteSuccess() {
        return commandExecuteSuccess;
    }

    public String getNotUsingInternalProxy() {
        return notUsingInternalProxy;
    }

    @NotNull
    public String format(@NotNull String message, @NotNull Object... replacements) {
        message = message.replace("%prefix%", prefix).replace("&", "§");
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
        buffer.writeString(this.notUsingInternalProxy);
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
        this.notUsingInternalProxy = buffer.readString();
    }
}
