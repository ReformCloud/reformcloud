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
package systems.reformcloud.reformcloud2.executor.api.common.command;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import systems.reformcloud.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.ConsoleCommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.command.sources.ConsoleCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.manager.DefaultCommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.language.loading.LanguageWorker;

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

        assertNotNull(commandManager.unregisterAndGetCommand("test"));
    }

    public static class TestCommand extends ConsoleCommand {

        TestCommand() {
            super("test", "test.command", "A test command", Collections.singletonList("ttt"));
        }

        @Override
        public boolean handleCommand(@NotNull CommandSource commandSource, @NotNull String[] strings) {
            System.out.println(commandSource.getName());
            return false;
        }
    }
}
