package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.source;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.ConsoleCommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;

import java.util.Arrays;
import java.util.Collection;

public final class MemoryCachedCommandSource extends ConsoleCommandSource {

    public MemoryCachedCommandSource(@NotNull Collection<String> messageCache, @NotNull CommandManager manager) {
        super(manager);
        this.messageCache = messageCache;
    }

    private final Collection<String> messageCache;

    @Override
    public void sendMessage(@NotNull String message) {
        messageCache.add(message);
    }

    @Override
    public void sendRawMessage(@NotNull String message) {
        messageCache.add(message);
    }

    @Override
    public void sendMessages(@NotNull String[] messages) {
        messageCache.addAll(Arrays.asList(messages));
    }

    @Override
    public void sendRawMessages(@NotNull String[] messages) {
        messageCache.addAll(Arrays.asList(messages));
    }
}
