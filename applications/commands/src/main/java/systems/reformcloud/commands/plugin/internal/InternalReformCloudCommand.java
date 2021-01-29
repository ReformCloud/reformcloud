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
package systems.reformcloud.commands.plugin.internal;

import org.jetbrains.annotations.NotNull;
import systems.refomcloud.embedded.Embedded;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.group.process.player.PlayerAccessConfiguration;
import systems.reformcloud.network.channel.NetworkChannel;
import systems.reformcloud.network.channel.manager.ChannelManager;
import systems.reformcloud.process.ProcessInformation;
import systems.reformcloud.process.ProcessState;
import systems.reformcloud.wrappers.ProcessWrapper;
import systems.reformcloud.shared.parser.Parsers;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

public final class InternalReformCloudCommand {

  private static final String PROCESS_DISPLAY_FORMAT = " * §7%s §8| §7%s §8| §7%d connected §8| %s";

  private InternalReformCloudCommand() {
    throw new UnsupportedOperationException();
  }

  public static void execute(@NotNull Consumer<String> messageSender,
                             @NotNull String[] strings, @NotNull String prefix,
                             @NotNull String commandSuccessMessage, @NotNull String anyAlias
  ) {
    if (strings.length == 1 && strings[0].equalsIgnoreCase("list")) {
      ExecutorAPI.getInstance()
        .getProcessProvider()
        .getProcessesAsync()
        .onComplete(processes -> {
          for (ProcessInformation process : processes) {
            String state = "§coffline";
            if (process.getCurrentState() == ProcessState.STARTED) {
              state = "§econnecting";
            } else if (process.getCurrentState().isOnline()) {
              state = "§aonline";
            }

            messageSender.accept(String.format(
              PROCESS_DISPLAY_FORMAT,
              process.getName(),
              process.getId().getNodeName(),
              process.getOnlineCount(),
              state
            ));
          }
        });
      return;
    }

    if (strings.length == 2) {
      switch (strings[0].toLowerCase()) {
        case "copy": {
          ExecutorAPI.getInstance()
            .getProcessProvider()
            .getProcessByNameAsync(strings[1])
            .onComplete(process -> {
              if (!process.isPresent()) {
                messageSender.accept(prefix + "§cThis process is unknown");
                return;
              }

              ProcessWrapper wrapper = process.get();
              wrapper.copy(wrapper.getProcessInformation().getPrimaryTemplate());
              messageSender.accept(commandSuccessMessage);
            });
          return;
        }

        case "start": {
          ExecutorAPI.getInstance()
            .getProcessGroupProvider()
            .getProcessGroupAsync(strings[1])
            .thenAccept(processGroup -> {
              if (!processGroup.isPresent()) {
                messageSender.accept(prefix + "§cThe specified group is unknown");
                return;
              }

              ExecutorAPI.getInstance()
                .getProcessProvider()
                .createProcess()
                .group(strings[1])
                .prepare()
                .onComplete(processWrapper -> processWrapper.setRuntimeState(ProcessState.STARTED));
              messageSender.accept(commandSuccessMessage);
            });
          return;
        }

        case "stop": {
          ExecutorAPI.getInstance()
            .getProcessProvider()
            .getProcessByNameAsync(strings[1])
            .onComplete(processWrapper -> {
              if (!processWrapper.isPresent()) {
                messageSender.accept(prefix + "§cThis process is unknown");
                return;
              }

              processWrapper.get().setRuntimeState(ProcessState.STOPPED);
              messageSender.accept(commandSuccessMessage);
            });
          return;
        }

        case "maintenance": {
          ExecutorAPI.getInstance()
            .getProcessGroupProvider()
            .getProcessGroupAsync(strings[1])
            .onComplete(processGroup -> {
              if (!processGroup.isPresent()) {
                messageSender.accept(prefix + "§cThe specified group is unknown");
                return;
              }

              PlayerAccessConfiguration configuration = processGroup.get().getPlayerAccessConfiguration();
              configuration.setMaintenance(!configuration.isMaintenance());
              processGroup.get().update();
              messageSender.accept(commandSuccessMessage);
            });
          return;
        }

        default:
          break;
      }
    }

    if (strings.length == 3) {
      if ("start".equalsIgnoreCase(strings[0])) {
        Integer count = Parsers.INT.parse(strings[2]);
        if (count == null || count <= 0) {
          messageSender.accept(prefix + "§cPlease provide a valid count!");
          return;
        }

        for (int started = 1; started <= count; started++) {
          ExecutorAPI.getInstance().getProcessProvider()
            .createProcess()
            .group(strings[1])
            .prepare()
            .onComplete(wrapper -> wrapper.setRuntimeState(ProcessState.STARTED));
        }

        messageSender.accept(commandSuccessMessage);
        return;
      }
    }

    if (strings.length >= 3 && strings[0].equalsIgnoreCase("execute")) {
      StringBuilder stringBuilder = new StringBuilder();
      for (String s : Arrays.copyOfRange(strings, 2, strings.length)) {
        stringBuilder.append(s).append(" ");
      }

      ExecutorAPI.getInstance()
        .getProcessProvider()
        .getProcessByNameAsync(strings[1])
        .onComplete(processWrapper -> {
          if (!processWrapper.isPresent()) {
            messageSender.accept(prefix + "§cThis process is unknown");
            return;
          }

          processWrapper.get().sendCommand(stringBuilder.toString());
          messageSender.accept(commandSuccessMessage);
        });
      return;
    }

    if (strings.length >= 2 && strings[0].equalsIgnoreCase("cmd")) {
      StringBuilder stringBuilder = new StringBuilder();
      for (String s : Arrays.copyOfRange(strings, 1, strings.length)) {
        stringBuilder.append(s).append(" ");
      }

      Optional<NetworkChannel> channel = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class).getFirstChannel();
      if (!channel.isPresent()) {
        messageSender.accept(prefix + "§cThe target node is not connected");
        return;
      }

      ExecutorAPI.getInstance()
        .getNodeInformationProvider()
        .getNodeInformationAsync(Embedded.getInstance().getCurrentProcessInformation().getId().getNodeUniqueId())
        .onComplete(nodeProcessWrapper -> {
          if (!nodeProcessWrapper.isPresent()) {
            messageSender.accept(prefix + "§cAn internal error occurred");
            return;
          }

          nodeProcessWrapper.get().sendCommandLineAsync(stringBuilder.toString()).onComplete(result -> {
            for (String s : result) {
              messageSender.accept("§7" + s);
            }
          });
          messageSender.accept(commandSuccessMessage);
        });
      return;
    }

    messageSender.accept(prefix + "§7/" + anyAlias + " list");
    messageSender.accept(prefix + "§7/" + anyAlias + " copy <name>");
    messageSender.accept(prefix + "§7/" + anyAlias + " start <group>");
    messageSender.accept(prefix + "§7/" + anyAlias + " start <group> <amount>");
    messageSender.accept(prefix + "§7/" + anyAlias + " stop <name>");
    messageSender.accept(prefix + "§7/" + anyAlias + " execute <name> <command>");
    messageSender.accept(prefix + "§7/" + anyAlias + " maintenance <group>");
    messageSender.accept(prefix + "§7/" + anyAlias + " cmd <command>");
  }
}
