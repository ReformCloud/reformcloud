package systems.reformcloud.reformcloud2.executor.api.common.commands.source;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionHolder;

public interface CommandSource extends PermissionHolder {

    /**
     * Sends a message to the source of the command
     *
     * @param message The message which should be sent
     */
    void sendMessage(@NotNull String message);

    /**
     * Sends a raw message to the source of the command
     *
     * @param message The message which should be sent
     */
    void sendRawMessage(@NotNull String message);

    /**
     * Sends many messages to the source of the command
     *
     * @param messages The messages which should be sent
     */
    void sendMessages(@NotNull String[] messages);

    /**
     * Sends many messages to the source of the command
     *
     * @param messages The messages which should be sent
     */
    void sendRawMessages(@NotNull String[] messages);

    /**
     * @return The command manger of the source
     */
    @NotNull
    CommandManager commandManager();
}
