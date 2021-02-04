/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
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
package systems.reformcloud.shared.command;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import systems.reformcloud.command.Command;
import systems.reformcloud.command.CommandContainer;
import systems.reformcloud.command.CommandManager;
import systems.reformcloud.command.CommandSender;
import systems.reformcloud.shared.StringUtil;
import systems.reformcloud.shared.command.sources.ConsoleCommandSender;

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

      Properties properties = StringUtil.parseProperties(strings, 0);
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
