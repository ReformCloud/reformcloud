package systems.reformcloud.reformcloud2.executor.api.common.groups.messages;

import com.google.gson.reflect.TypeToken;

import java.text.MessageFormat;

public class IngameMessages {

    public static final TypeToken<IngameMessages> TYPE = new TypeToken<IngameMessages>() {};

    public IngameMessages() {
    }

    public IngameMessages(String prefix, String processStarted, String processConnected, String processStopped,
                          String noHubServerAvailable, String alreadyConnectedToHub, String connectingToHub,
                          String currentProcessClosed) {
        this.prefix = prefix;
        this.processStarted = processStarted;
        this.processConnected = processConnected;
        this.processStopped = processStopped;
        this.noHubServerAvailable = noHubServerAvailable;
        this.alreadyConnectedToHub = alreadyConnectedToHub;
        this.connectingToHub = connectingToHub;
        this.currentProcessClosed = currentProcessClosed;
    }

    private String prefix = "§6§lR§e§leform§6§lC§e§lloud§6§l2 §7|";

    private String processStarted = "%prefix% [§e§l+§7] {0}";

    private String processConnected = "%prefix% [§a§l+§7] {0}";

    private String processStopped = "%prefix% [§4§l-§7] {0}";

    private String noHubServerAvailable = "%prefix% There is currently §cno §7Hub-Server available";

    private String alreadyConnectedToHub = "%prefix% You are §ealready connected to a §6Hub-Server";

    private String connectingToHub = "%prefix% §aSending §7to hub §6{0}§7...";

    private String currentProcessClosed = "%prefix% §7The current process got §cstopped";

    private String commandExecuteSuccess = "%prefix% §aSuccessfully §7proceeded!";

    public String getPrefix() {
        return prefix;
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

    public String getCommandExecuteSuccess() {
        return commandExecuteSuccess;
    }

    public String format(String message, Object... replacements) {
        message = message.replace("%prefix%", prefix).replace("&", "§");
        return MessageFormat.format(message, replacements);
    }
}
