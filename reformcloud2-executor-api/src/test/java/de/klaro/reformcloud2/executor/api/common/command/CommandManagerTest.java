package de.klaro.reformcloud2.executor.api.common.command;

import de.klaro.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import de.klaro.reformcloud2.executor.api.common.commands.Command;
import de.klaro.reformcloud2.executor.api.common.commands.basic.ConsoleCommandSource;
import de.klaro.reformcloud2.executor.api.common.commands.basic.command.sources.ConsoleCommand;
import de.klaro.reformcloud2.executor.api.common.commands.basic.manager.DefaultCommandManager;
import de.klaro.reformcloud2.executor.api.common.commands.manager.CommandManager;
import de.klaro.reformcloud2.executor.api.common.commands.source.CommandSource;
import de.klaro.reformcloud2.executor.api.common.language.loading.LanguageWorker;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public final class CommandManagerTest {

    @Test
    public void testCommandManager() {
        CommandManager commandManager = new DefaultCommandManager();
        Command command = new TestCommand();
        LanguageWorker.doLoad();

        assertNotNull(commandManager);
        assertNotNull(command);

        commandManager.register(command);

        assertEquals(1, commandManager.getCommands().size());

        commandManager.dispatchCommand(new ConsoleCommandSource(commandManager), AllowedCommandSources.REST, "test", s -> assertEquals(s, "The command source REST is not allowed"));

        commandManager.dispatchCommand(new ConsoleCommandSource(commandManager), AllowedCommandSources.CONSOLE, "test", s -> {
            throw new IllegalStateException();
        });

        commandManager.dispatchCommand(new ConsoleCommandSource(commandManager), AllowedCommandSources.CONSOLE, "ttt", s -> {
            throw new IllegalStateException();
        });

        assertNotNull(commandManager.unregisterAndGetCommand("test"));
    }

    public static class TestCommand extends ConsoleCommand {

        TestCommand() {
            super("test", "test.command", "A test command", Collections.singletonList("ttt"));
        }

        @Override
        public boolean handleCommand(CommandSource commandSource, String[] strings) {
            System.out.println(commandSource.getName());
            return false;
        }
    }
}
