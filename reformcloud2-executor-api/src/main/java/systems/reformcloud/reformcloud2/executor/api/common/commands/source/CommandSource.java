package systems.reformcloud.reformcloud2.executor.api.common.commands.source;

import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionHolder;

public interface CommandSource extends PermissionHolder {

    void sendMessage(String message);

    void sendRawMessage(String message);

    void sendMessages(String[] messages);

    void sendRawMessages(String[] messages);

    CommandManager commandManager();
}
