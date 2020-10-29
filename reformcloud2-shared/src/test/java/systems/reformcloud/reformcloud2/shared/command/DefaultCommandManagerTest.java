package systems.reformcloud.reformcloud2.shared.command;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import systems.reformcloud.reformcloud2.executor.api.command.Command;
import systems.reformcloud.reformcloud2.executor.api.command.CommandContainer;
import systems.reformcloud.reformcloud2.executor.api.command.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.command.CommandSender;
import systems.reformcloud.reformcloud2.executor.api.utility.StringUtil;
import systems.reformcloud.reformcloud2.shared.command.sources.ConsoleCommandSender;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DefaultCommandManagerTest {

    private final CommandManager commandManager = new DefaultCommandManager();

    @Test
    @Order(1)
    void testCommandRegister() {
        this.commandManager.registerCommand(new TestCommand(), "A test command", "test", "t");
        Assertions.assertEquals(1, this.commandManager.getCommands().size());

        Assertions.assertNotNull(this.commandManager.getCommand("test").orElse(null));
        Assertions.assertNotNull(this.commandManager.getCommand("t").orElse(null));
    }

    @Test
    @Order(2)
    void testCommandGet() {
        CommandContainer commandContainer = this.commandManager.getCommand("test").orElse(null);
        Assertions.assertNotNull(commandContainer);
        Assertions.assertEquals(commandContainer.getDescription(), "A test command");
        Assertions.assertEquals(2, commandContainer.getAliases().size());
    }

    @Test
    @Order(3)
    void testCommandAllCommands() {
        Assertions.assertEquals(1, this.commandManager.getCommands().size());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> this.commandManager.getCommands().add(
            new DefaultCommandContainer(Collections.singletonList("v"), "test", new TestCommand())
        ));
    }

    @Test
    @Order(4)
    void testCommandProcess() {
        Assertions.assertTrue(this.commandManager.process("test 54gr 44tg4 t4t44t4 test=true", ConsoleCommandSender.INSTANCE));
    }

    @Test
    @Order(5)
    void testCommandSuggest() {
        List<String> suggest = this.commandManager.suggest("test 45 343 432", ConsoleCommandSender.INSTANCE);
        Assertions.assertEquals(1, suggest.size());
        Assertions.assertEquals("test", suggest.get(0));
    }

    @Test
    @Order(6)
    void testCommandUnregister() {
        this.commandManager.unregisterCommand("test");
        Assertions.assertEquals(0, this.commandManager.getCommands().size());
        Assertions.assertNull(this.commandManager.getCommand("t").orElse(null));
    }

    private static class TestCommand implements Command {

        @Override
        public void process(@NotNull CommandSender sender, @NonNls String[] strings, @NotNull String commandLine) {
            Assertions.assertEquals(4, strings.length);
            Assertions.assertEquals("test 54gr 44tg4 t4t44t4 test=true", commandLine);

            Properties properties = StringUtil.calcProperties(strings, 0);
            Assertions.assertEquals(1, properties.size());
            Assertions.assertEquals("true", properties.getProperty("test"));
        }

        @Override
        public @NotNull List<String> suggest(@NotNull CommandSender commandSender, @NonNls String[] strings, int bufferIndex, @NotNull String commandLine) {
            Assertions.assertEquals(3, strings.length);
            Assertions.assertEquals("test 45 343 432", commandLine);
            Assertions.assertEquals(2, bufferIndex);
            return Collections.singletonList("test");
        }
    }
}
