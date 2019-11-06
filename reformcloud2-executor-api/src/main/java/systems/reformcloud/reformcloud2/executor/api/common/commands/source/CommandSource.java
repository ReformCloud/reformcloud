package systems.reformcloud.reformcloud2.executor.api.common.commands.source;

import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionHolder;

import javax.annotation.Nonnull;

public interface CommandSource extends PermissionHolder {

    /**
     * Sends a message to the source of the command
     *
     * @param message The message which should be sent
     */
    void sendMessage(String message);

    /**
     * Sends a raw message to the source of the command
     *
     * @param message The message which should be sent
     */
    void sendRawMessage(String message);

    /**
     * Sends many messages to the source of the command
     *
     * @param messages The messages which should be sent
     */
    void sendMessages(String[] messages);

    /**
     * Sends many messages to the source of the command
     *
     * @param messages The messages which should be sent
     */
    void sendRawMessages(String[] messages);

    /**
     * @return The command manger of the source
     */
    @Nonnull
    CommandManager commandManager();
}
