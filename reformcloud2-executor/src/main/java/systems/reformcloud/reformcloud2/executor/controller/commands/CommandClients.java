package systems.reformcloud.reformcloud2.executor.controller.commands;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.process.JavaProcessHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;
import systems.reformcloud.reformcloud2.executor.client.config.ClientConfig;
import systems.reformcloud.reformcloud2.executor.client.config.ClientConnectionConfig;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;
import systems.reformcloud.reformcloud2.executor.controller.process.ClientManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

import static java.util.concurrent.TimeUnit.SECONDS;

public final class CommandClients extends GlobalCommand {

    public CommandClients() {
        super("clients", "reformcloud.command.clients", "Manages the clients", "cli");
    }

    @Override
    public void describeCommandToSender(@NotNull CommandSource source) {
        source.sendMessages((
                "clients list           | Lists all connected clients\n" +
                        "clients info <name>    | Shows information about a connected client\n" +
                        " \n" +
                        "clients internal       | Base command to show the status of the internal client\n" +
                        " --start=true          | Starts the internal client\n" +
                        " --stop=true           | Stops the internal client\n" +
                        " --create=true         | Creates the internal client\n" +
                        " --delete=true         | Deletes the internal client"
        ).split("\n"));
    }

    @Override
    public boolean handleCommand(@NotNull CommandSource commandSource, @NotNull String[] strings) {
        if (strings.length == 0) {
            this.describeCommandToSender(commandSource);
            return true;
        }

        if (strings.length == 1 && strings[0].equalsIgnoreCase("list")) {
            this.listClientsToSender(commandSource);
            return true;
        }

        if (strings.length == 2 && strings[0].equalsIgnoreCase("info")) {
            ClientRuntimeInformation information = ClientManager.INSTANCE.getClientInfo(strings[1]);
            if (information == null) {
                commandSource.sendMessage(LanguageManager.get("command-clients-client-not-connected", strings[1]));
                return true;
            }

            this.describeClientToSender(commandSource, information);
            return true;
        }

        if (strings.length == 1 && strings[0].equalsIgnoreCase("internal")) {
            commandSource.sendMessage(LanguageManager.get(
                    "command-clients-internal-client-info",
                    ClientManager.INSTANCE.getProcess() == null ? "&coffline&r" : "&aonline&r"
            ));
            return true;
        }

        Properties properties = StringUtil.calcProperties(strings, 1);
        if (strings[0].equalsIgnoreCase("internal")) {
            if (properties.containsKey("start")) {
                if (ClientManager.INSTANCE.getProcess() != null) {
                    commandSource.sendMessage(LanguageManager.get("command-clients-internal-client-already-started"));
                    return true;
                }

                if (!this.existsInternalClient()) {
                    commandSource.sendMessage(LanguageManager.get("command-clients-internal-client-does-not-exists"));
                    return true;
                }

                try {
                    Process process = new ProcessBuilder()
                            .command(Arrays.asList("java", "-jar", "runner.jar").toArray(new String[0]))
                            .directory(new File("reformcloud/.client"))
                            .start();
                    ClientManager.INSTANCE.setProcess(process);
                    commandSource.sendMessage(LanguageManager.get("command-clients-internal-client-start-successful"));
                } catch (final IOException ex) {
                    ex.printStackTrace();
                }
                return true;
            }

            if (properties.containsKey("stop")) {
                if (ClientManager.INSTANCE.getProcess() == null) {
                    commandSource.sendMessage(LanguageManager.get("command-clients-internal-client-not-started"));
                    return true;
                }

                JavaProcessHelper.shutdown(ClientManager.INSTANCE.getProcess(), true, true, SECONDS.toMillis(5), "stop");
                commandSource.sendMessage(LanguageManager.get("command-clients-internal-client-stop-successful"));
                return true;
            }

            if (properties.containsKey("create")) {
                if (this.existsInternalClient()) {
                    commandSource.sendMessage(LanguageManager.get("command-clients-internal-client-already-exists"));
                    return true;
                }

                this.setupInternalClient(commandSource);
                return true;
            }

            if (properties.containsKey("delete")) {
                if (!this.existsInternalClient()) {
                    commandSource.sendMessage(LanguageManager.get("command-clients-internal-client-does-not-exists"));
                    return true;
                }

                if (ClientManager.INSTANCE.getProcess() != null) {
                    JavaProcessHelper.shutdown(ClientManager.INSTANCE.getProcess(), true, true, SECONDS.toMillis(5), "stop");
                }

                SystemHelper.deleteDirectory(Paths.get("reformcloud/.client"));
                commandSource.sendMessage(LanguageManager.get("command-clients-internal-client-deleted"));
                return true;
            }
        }

        this.describeCommandToSender(commandSource);
        return true;
    }

    private boolean existsInternalClient() {
        return Files.exists(Paths.get("reformcloud/.client"));
    }

    private void describeClientToSender(CommandSource source, ClientRuntimeInformation information) {
        AtomicReference<StringBuilder> builder = new AtomicReference<>(new StringBuilder());

        builder.get().append(" > Name          - ").append(information.getName()).append("\n");
        builder.get().append(" > Start-Host    - ").append(information.startHost()).append("\n");
        builder.get().append(" > Max-Memory    - ").append(information.maxMemory()).append("MB\n");
        builder.get().append(" > Max-Processes - ").append(information.maxProcessCount());

        source.sendMessages(builder.get().toString().split("\n"));
    }

    private void listClientsToSender(CommandSource source) {
        StringBuilder stringBuilder = new StringBuilder();

        Collection<ClientRuntimeInformation> clientRuntimeInformation = ClientManager.INSTANCE.getClientRuntimeInformation();

        stringBuilder.append("Connected client (").append(clientRuntimeInformation.size()).append(")").append("\n");
        for (ClientRuntimeInformation runtimeInformation : clientRuntimeInformation) {
            stringBuilder.append(" > ").append(runtimeInformation.getName()).append("/").append(runtimeInformation.startHost()).append("\n");
        }

        source.sendMessages(stringBuilder.toString().split("\n"));
    }

    private void setupInternalClient(CommandSource source) {
        if (ControllerExecutor.getInstance().getControllerConfig().getNetworkListener().isEmpty()) {
            source.sendMessage(LanguageManager.get("command-clients-controller-no-network-listeners"));
            return;
        }

        Map.Entry<String, Integer> map = ControllerExecutor.getInstance().getControllerConfig().getNetworkListener().get(
                new Random().nextInt(ControllerExecutor.getInstance().getControllerConfig().getNetworkListener().size())
        ).entrySet().iterator().next();

        SystemHelper.createDirectory(Paths.get("reformcloud/.client/reformcloud/.bin"));
        SystemHelper.createDirectory(Paths.get("reformcloud/.client/reformcloud/files/.connection"));
        SystemHelper.doCopy("reformcloud/.bin/config.properties", "reformcloud/.client/reformcloud/.bin/config.properties");
        SystemHelper.doCopy("reformcloud/.bin/executor.jar", "reformcloud/.client/reformcloud/.bin/executor.jar");
        SystemHelper.doCopy("reformcloud/.bin/connection.json", "reformcloud/.client/reformcloud/files/.connection/connection.json");
        CommonHelper.rewriteProperties(
                "reformcloud/.client/reformcloud/.bin/config.properties",
                "ReformCloudController edit",
                (UnaryOperator<String>) s -> {
                    if (s.equals("reformcloud.type.id")) {
                        s = "2";
                    }

                    return s;
                }
        );
        new JsonConfiguration().add("config", new ClientConfig(
                CommonHelper.calculateMaxMemory(),
                -1,
                90.0D,
                map.getKey()
        )).write(Paths.get("reformcloud/.client/" + ClientConfig.PATH));
        new JsonConfiguration().add("config", new ClientConnectionConfig(
                map.getKey(),
                map.getValue()
        )).write("reformcloud/.client/" + ClientConnectionConfig.PATH);
        DownloadHelper.downloadAndDisconnect(StringUtil.RUNNER_DOWNLOAD_URL, "reformcloud/.client/runner.jar");
        source.sendMessage(LanguageManager.get("command-clients-internal-client-created"));
    }
}
