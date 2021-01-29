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
package systems.reformcloud.wrappers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.node.NodeInformation;
import systems.reformcloud.task.Task;

import java.util.Collection;
import java.util.Optional;

public interface NodeProcessWrapper {

  /**
   * @return The node information the wrapper is based on
   */
  @NotNull
  NodeInformation getNodeInformation();

  /**
   * Requests a node information update of the node the wrapper is based on
   *
   * @return An optional which is present if the node is still connected and sent an updated node information back
   */
  @NotNull
  Optional<NodeInformation> requestNodeInformationUpdate();

  /**
   * Sends a command to the node this wrapper is based on
   *
   * @param commandLine The command which should get sent to the node
   * @return The console message result of the command
   */
  @NotNull
  @UnmodifiableView Collection<String> sendCommandLine(@NotNull String commandLine);

  /**
   * Tab completes a command in the node the wrapper is based on
   *
   * @param commandLine The command line to tab complete
   * @return The tab complete results of the request
   */
  @NotNull
  @UnmodifiableView Collection<String> tabCompleteCommandLine(@NotNull String commandLine);

  /**
   * This method does the same as {@link #getNodeInformation()} but asynchronously.
   *
   * @return The node information the wrapper is based on
   */
  @NotNull
  default Task<NodeInformation> getNodeInformationAsync() {
    return Task.supply(this::getNodeInformation);
  }

  /**
   * This method does the same as {@link #requestNodeInformationUpdate()} but asynchronously.
   *
   * @return An optional which is present if the node is still connected and sent an updated node information back
   */
  @NotNull
  default Task<Optional<NodeInformation>> requestNodeInformationUpdateAsync() {
    return Task.supply(this::requestNodeInformationUpdate);
  }

  /**
   * This method does the same as {@link #sendCommandLine(String)} but asynchronously.
   *
   * @param commandLine The command which should get sent to the node
   * @return The console message result of the command
   */
  @NotNull
  default Task<Collection<String>> sendCommandLineAsync(@NotNull String commandLine) {
    return Task.supply(() -> this.sendCommandLine(commandLine));
  }

  /**
   * This method does the same as {@link #tabCompleteCommandLine(String)} but asynchronously.
   *
   * @param commandLine The command line to tab complete
   * @return The tab complete results of the request
   */
  @NotNull
  default Task<Collection<String>> tabCompleteCommandLineAsync(@NotNull String commandLine) {
    return Task.supply(() -> this.tabCompleteCommandLine(commandLine));
  }
}
