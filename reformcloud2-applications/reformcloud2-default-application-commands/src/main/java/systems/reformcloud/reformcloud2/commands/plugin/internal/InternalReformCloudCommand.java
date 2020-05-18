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
package systems.reformcloud.reformcloud2.commands.plugin.internal;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.util.Arrays;
import java.util.function.Consumer;

public final class InternalReformCloudCommand {

    private static final String PROCESS_DISPLAY_FORMAT = " * §7%s §8| §7%s §8| §7%d§8/§7%d §8| %s";

    private InternalReformCloudCommand() {
        throw new UnsupportedOperationException();
    }

    public static void execute(@NotNull Consumer<String> messageSender,
                               @NotNull String[] strings, @NotNull String prefix,
                               @NotNull String commandSuccessMessage, @NotNull String anyAlias
    ) {
        if (strings.length == 1 && strings[0].equalsIgnoreCase("list")) {
            ExecutorAPI.getInstance()
                    .getAsyncAPI()
                    .getProcessAsyncAPI()
                    .getAllProcessesAsync()
                    .onComplete(processes -> {
                        for (ProcessInformation process : processes) {
                            String state = "§coffline";
                            if (process.getProcessDetail().getProcessState().isValid() && !process.getNetworkInfo().isConnected()) {
                                state = "§econnecting";
                            } else if (process.getProcessDetail().getProcessState().isReady()) {
                                state = "§aonline";
                            }

                            messageSender.accept(String.format(
                                    PROCESS_DISPLAY_FORMAT,
                                    process.getProcessDetail().getName(),
                                    process.getProcessDetail().getParentName(),
                                    process.getProcessPlayerManager().getOnlineCount(),
                                    process.getProcessDetail().getMaxPlayers(),
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
                            .getAsyncAPI()
                            .getProcessAsyncAPI()
                            .getProcessAsync(strings[1])
                            .onComplete(process -> {
                                if (process == null) {
                                    messageSender.accept(prefix + "§cThis process is unknown");
                                    return;
                                }

                                process.toWrapped().copy();
                            });
                    messageSender.accept(commandSuccessMessage);
                    return;
                }

                case "start": {
                    ExecutorAPI.getInstance()
                            .getAsyncAPI()
                            .getProcessAsyncAPI()
                            .startProcessAsync(strings[1]);
                    messageSender.accept(commandSuccessMessage);
                    return;
                }

                case "stop": {
                    ExecutorAPI.getInstance()
                            .getAsyncAPI()
                            .getProcessAsyncAPI()
                            .stopProcessAsync(strings[1]);
                    messageSender.accept(commandSuccessMessage);
                    return;
                }

                case "stopall": {
                    ExecutorAPI.getInstance()
                            .getAsyncAPI()
                            .getProcessAsyncAPI()
                            .getProcessesAsync(strings[1])
                            .onComplete(results -> {
                                for (ProcessInformation result : results) {
                                    ExecutorAPI.getInstance().getAsyncAPI().getProcessAsyncAPI().stopProcessAsync(result);
                                }
                            });
                    messageSender.accept(commandSuccessMessage);
                    return;
                }

                case "maintenance": {
                    ExecutorAPI.getInstance()
                            .getAsyncAPI()
                            .getGroupAsyncAPI()
                            .getProcessGroupAsync(strings[1])
                            .onComplete(processGroup -> {
                                if (processGroup == null) {
                                    messageSender.accept(prefix + "§cThis group is unknown");
                                    return;
                                }

                                processGroup.getPlayerAccessConfiguration().toggleMaintenance();
                                ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().updateProcessGroup(processGroup);
                            });
                    messageSender.accept(commandSuccessMessage);
                    return;
                }
            }
        }

        if (strings.length == 3) {
            switch (strings[0].toLowerCase()) {
                case "start": {
                    Integer count = CommonHelper.fromString(strings[2]);
                    if (count == null || count <= 0) {
                        messageSender.accept(prefix + "§cPlease provide a valid count!");
                        return;
                    }

                    Task.EXECUTOR.execute(() -> {
                        for (int started = 1; started <= count; started++) {
                            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().startProcess(strings[1]);
                        }
                    });

                    messageSender.accept(commandSuccessMessage);
                    return;
                }

                case "ofall": {
                    if (strings[2].equalsIgnoreCase("stop")) {
                        ExecutorAPI.getInstance()
                                .getAsyncAPI()
                                .getGroupAsyncAPI()
                                .getMainGroupAsync(strings[1])
                                .onComplete(mainGroup -> {
                                    if (mainGroup == null) {
                                        messageSender.accept(prefix + "§cThis main group is unknown");
                                        return;
                                    }

                                    for (String subGroup : mainGroup.getSubGroups()) {
                                        ExecutorAPI.getInstance()
                                                .getAsyncAPI()
                                                .getProcessAsyncAPI()
                                                .getProcessesAsync(subGroup)
                                                .onComplete(processes -> {
                                                    for (ProcessInformation process : processes) {
                                                        ExecutorAPI.getInstance()
                                                                .getAsyncAPI()
                                                                .getProcessAsyncAPI()
                                                                .stopProcessAsync(process);
                                                    }
                                                });
                                    }
                                });
                        messageSender.accept(commandSuccessMessage);
                        return;
                    }
                    break;
                }
            }
        }

        if (strings.length >= 3 && strings[0].equalsIgnoreCase("execute")) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : Arrays.copyOfRange(strings, 2, strings.length)) {
                stringBuilder.append(s).append(" ");
            }

            ExecutorAPI.getInstance()
                    .getAsyncAPI()
                    .getProcessAsyncAPI()
                    .executeProcessCommandAsync(strings[1], stringBuilder.toString());
            messageSender.accept(commandSuccessMessage);
            return;
        }

        if (strings.length >= 2 && strings[0].equalsIgnoreCase("cmd")) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : Arrays.copyOfRange(strings, 1, strings.length)) {
                stringBuilder.append(s).append(" ");
            }

            ExecutorAPI.getInstance()
                    .getAsyncAPI()
                    .getConsoleAsyncAPI()
                    .dispatchConsoleCommandAndGetResultAsync(stringBuilder.toString())
                    .onComplete(messages -> {
                        for (String message : messages) {
                            messageSender.accept("§7" + message);
                        }
                    });
            messageSender.accept(commandSuccessMessage);
            return;
        }

        messageSender.accept(prefix + "§7/" + anyAlias + " list");
        messageSender.accept(prefix + "§7/" + anyAlias + " copy <name>");
        messageSender.accept(prefix + "§7/" + anyAlias + " start <group>");
        messageSender.accept(prefix + "§7/" + anyAlias + " start <group> <amount>");
        messageSender.accept(prefix + "§7/" + anyAlias + " stop <name>");
        messageSender.accept(prefix + "§7/" + anyAlias + " stopall <group>");
        messageSender.accept(prefix + "§7/" + anyAlias + " ofall <mainGroup> stop");
        messageSender.accept(prefix + "§7/" + anyAlias + " execute <name> <command>");
        messageSender.accept(prefix + "§7/" + anyAlias + " maintenance <group>");
        messageSender.accept(prefix + "§7/" + anyAlias + " cmd <command>");
    }
}
