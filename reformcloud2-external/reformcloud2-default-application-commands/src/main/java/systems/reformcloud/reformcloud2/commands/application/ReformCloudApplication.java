package systems.reformcloud.reformcloud2.commands.application;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.commands.application.listener.ProcessListener;
import systems.reformcloud.reformcloud2.commands.application.packet.out.PacketOutRegisterCommandsConfig;
import systems.reformcloud.reformcloud2.commands.config.CommandsConfig;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class ReformCloudApplication extends Application {

    private static CommandsConfig commandsConfig;

    private static final ProcessListener LISTENER = new ProcessListener();

    @Override
    public void onEnable() {
        final Path path = Paths.get(dataFolder().getPath(), "config.json");
        if (!Files.exists(path)) {
            SystemHelper.createDirectory(dataFolder().toPath());
            new JsonConfiguration()
                    .add("config", new CommandsConfig(
                            true, Arrays.asList("l", "leave", "lobby", "hub", "quit"),
                            true, Arrays.asList("reformcloud", "rc", "cloud")
                    )).write(path);
        }

        commandsConfig = JsonConfiguration.read(path).get("config", new TypeToken<CommandsConfig>() {});
        ExecutorAPI.getInstance().getEventManager().registerListener(LISTENER);
    }

    @Override
    public void onPreDisable() {
        commandsConfig = new CommandsConfig(false, new ArrayList<>(), false, new ArrayList<>());

        ExecutorAPI.getInstance().getEventManager().unregisterListener(LISTENER);
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses().stream().filter(e -> !e.getTemplate().isServer()).forEach(
                e -> DefaultChannelManager.INSTANCE.get(e.getName()).ifPresent(s -> s.sendPacket(new PacketOutRegisterCommandsConfig()))
        );
    }

    public static CommandsConfig getCommandsConfig() {
        return commandsConfig;
    }
}
