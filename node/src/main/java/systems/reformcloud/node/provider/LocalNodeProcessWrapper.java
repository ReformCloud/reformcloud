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
package systems.reformcloud.node.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.command.CommandManager;
import systems.reformcloud.node.NodeExecutor;
import systems.reformcloud.node.NodeInformation;
import systems.reformcloud.shared.command.sources.CachedCommandSender;
import systems.reformcloud.shared.command.sources.ConsoleCommandSender;
import systems.reformcloud.shared.node.DefaultNodeInformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class LocalNodeProcessWrapper extends DefaultNodeProcessWrapper {

  LocalNodeProcessWrapper(@NotNull DefaultNodeInformation nodeInformation) {
    super(nodeInformation);
  }

  @Override
  public @NotNull Optional<NodeInformation> requestNodeInformationUpdate() {
    return Optional.of(super.nodeInformation = NodeExecutor.getInstance().updateCurrentNodeInformation());
  }

  @Override
  public @NotNull @UnmodifiableView Collection<String> sendCommandLine(@NotNull String commandLine) {
    Collection<String> lines = new ArrayList<>();
    ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(CommandManager.class).process(commandLine, new CachedCommandSender(lines));
    return lines;
  }

  @Override
  public @NotNull @UnmodifiableView Collection<String> tabCompleteCommandLine(@NotNull String commandLine) {
    return ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(CommandManager.class).suggest(commandLine, ConsoleCommandSender.INSTANCE);
  }
}
