package systems.reformcloud.reformcloud2.commands.application;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.commands.application.listener.ProcessInclusionHandler;
import systems.reformcloud.reformcloud2.commands.application.packet.in.PacketInGetCommandsConfig;
import systems.reformcloud.reformcloud2.commands.application.update.CommandAddonUpdater;
import systems.reformcloud.reformcloud2.commands.config.CommandsConfig;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.common.application.updater.ApplicationUpdateRepository;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;

import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class ReformCloudApplication extends Application {

    private static ReformCloudApplication instance;

    private static CommandsConfig commandsConfig;

    private static final ApplicationUpdateRepository REPOSITORY = new CommandAddonUpdater();

    @Override
    public void onInstallable() {
        ExecutorAPI.getInstance().getEventManager().registerListener(new ProcessInclusionHandler());
    }

    @Override
    public void onLoad() {
        instance = this;
    }

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

        commandsConfig = JsonConfiguration.read(path).get("config", new TypeToken<CommandsConfig>() {
        });
        ExecutorAPI.getInstance().getPacketHandler().registerHandler(new PacketInGetCommandsConfig());
    }

    @Override
    public void onPreDisable() {
        ExecutorAPI.getInstance().getPacketHandler().unregisterNetworkHandlers(NetworkUtil.EXTERNAL_BUS + 1);
    }

    @Nullable
    @Override
    public ApplicationUpdateRepository getUpdateRepository() {
        return REPOSITORY;
    }

    public static CommandsConfig getCommandsConfig() {
        return commandsConfig;
    }

    public static ReformCloudApplication getInstance() {
        return instance;
    }
}
