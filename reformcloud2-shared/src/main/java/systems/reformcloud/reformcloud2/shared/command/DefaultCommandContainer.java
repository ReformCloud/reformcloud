/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
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
package systems.reformcloud.reformcloud2.shared.command;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.command.Command;
import systems.reformcloud.reformcloud2.executor.api.command.CommandContainer;
import systems.reformcloud.reformcloud2.executor.api.utility.MoreCollections;

import java.util.Collection;

public final class DefaultCommandContainer implements CommandContainer {

  private final Collection<String> aliases;
  private final Command command;
  private final String description;

  DefaultCommandContainer(Collection<String> aliases, String description, Command command) {
    this.aliases = MoreCollections.toLowerCase(aliases);
    this.command = command;
    this.description = description;
  }

  @NotNull
  @Override
  public Collection<String> getAliases() {
    return this.aliases;
  }

  @Override
  public @NotNull String getDescription() {
    return this.description;
  }

  @NotNull
  @Override
  public Command getCommand() {
    return this.command;
  }
}
