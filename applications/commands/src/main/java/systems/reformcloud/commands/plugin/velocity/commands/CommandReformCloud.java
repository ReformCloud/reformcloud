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
package systems.reformcloud.commands.plugin.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.embedded.Embedded;
import systems.refomcloud.embedded.plugin.velocity.VelocityExecutor;
import systems.reformcloud.commands.plugin.internal.InternalReformCloudCommand;

import java.util.List;

public class CommandReformCloud implements SimpleCommand {

  private final List<String> aliases;

  public CommandReformCloud(@NotNull List<String> aliases) {
    this.aliases = aliases;
  }

  @Override
  public void execute(Invocation invocation) {
    String prefix = Embedded.getInstance().getIngameMessages().getPrefix();
    InternalReformCloudCommand.execute(
      message -> invocation.source().sendMessage(Identity.nil(), VelocityExecutor.SERIALIZER.deserialize(message)),
      invocation.arguments(),
      prefix.endsWith(" ") ? prefix : prefix + " ",
      this.getCommandSuccessMessage(),
      this.aliases.isEmpty() ? "rc" : this.aliases.get(0)
    );
  }

  @Override
  public boolean hasPermission(Invocation invocation) {
    return invocation.source().hasPermission("reformcloud.command.reformcloud");
  }

  @NotNull
  private String getCommandSuccessMessage() {
    String message = Embedded.getInstance().getIngameMessages().getCommandExecuteSuccess();
    return Embedded.getInstance().getIngameMessages().format(message);
  }

  @NotNull
  public List<String> getAliases() {
    return this.aliases;
  }
}
