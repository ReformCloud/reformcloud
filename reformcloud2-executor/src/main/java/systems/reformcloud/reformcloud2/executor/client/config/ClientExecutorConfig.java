package systems.reformcloud.reformcloud2.executor.client.config;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.CommonHelper;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.logger.setup.Setup;
import systems.reformcloud.reformcloud2.executor.api.common.logger.setup.basic.DefaultSetup;
import systems.reformcloud.reformcloud2.executor.api.common.logger.setup.basic.DefaultSetupQuestion;
import systems.reformcloud.reformcloud2.executor.client.ClientExecutor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import static systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links.newCollection;
import static systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper.createDirectory;

public final class ClientExecutorConfig {

    private final Setup setup = new DefaultSetup();

    private static final Collection<Path> PATHS = newCollection(
            s -> Paths.get(s),
            "reformcloud/temp",
            "reformcloud/static",
            "reformcloud/applications",
            "reformcloud/templates",
            "reformcloud/global/plugins",
            "reformcloud/files",
            "reformcloud/files/.connection"
    );

    private final ClientConfig clientConfig;

    private final ClientConnectionConfig clientConnectionConfig;

    private final String connectionKey;

    public ClientExecutorConfig() {
        createDirectories();
        if (!Files.exists(ClientConfig.PATH) || !Files.exists(ClientConnectionConfig.PATH)) {
            firstSetup();
        }

        this.clientConfig = JsonConfiguration.read(ClientConfig.PATH).get("config", new TypeToken<ClientConfig>() {});
        this.clientConnectionConfig = JsonConfiguration.read(ClientConnectionConfig.PATH).get("config", new TypeToken<ClientConnectionConfig>() {});
        this.connectionKey = JsonConfiguration.read("reformcloud/files/.connection/connection.json").getString("key");
    }

    private void firstSetup() {
        AtomicReference<String> startHost = new AtomicReference<>();
        AtomicReference<String> controllerHost = new AtomicReference<>();
        setup.addQuestion(new DefaultSetupQuestion("Please copy the connection key into the console (controller/reformcloud/.bin/connection.json)",
                "Please copy the real key",
                s -> true,
                s -> new JsonConfiguration().add("key", s).write("reformcloud/files/.connection/connection.json"))
        ).addQuestion(new DefaultSetupQuestion("Please write the start host or domain name",
                "Please write an ip address or domain name",
                s -> CommonHelper.getIpAddress(s.trim()) != null,
                s -> startHost.set(CommonHelper.getIpAddress(s.trim())))
        ).addQuestion(new DefaultSetupQuestion("Please enter the max memory of the client", "Please write a number bigger than 128",
                s -> {
                    try {
                        int i = Integer.parseInt(s);
                        return i > 128;
                    } catch (final Throwable throwable) {
                        return false;
                    }
                },
                s -> new JsonConfiguration()
                        .add("config", new ClientConfig(Integer.parseInt(s), -1, 99.0, startHost.get()))
                        .write(ClientConfig.PATH))
        ).addQuestion(new DefaultSetupQuestion("Please write the ip address or domain name of the controller",
                "Please write the real ip or domain ;)",
                s -> CommonHelper.getIpAddress(s.trim()) != null,
                s -> controllerHost.set(CommonHelper.getIpAddress(s.trim())))
        ).addQuestion(new DefaultSetupQuestion("Please write the controller network port (default: 2008)", "The port must be bigger than 0",
                s -> {
                    try {
                        int i = Integer.parseInt(s);
                        return i > 0;
                    } catch (final Throwable throwable) {
                        return false;
                    }
                },
                s -> new JsonConfiguration()
                        .add("config", new ClientConnectionConfig(controllerHost.get(), Integer.parseInt(s)))
                        .write(ClientConnectionConfig.PATH))
        ).startSetup(ClientExecutor.getInstance().getLoggerBase());
    }

    private void createDirectories() {
        PATHS.forEach(path -> {
            if (!Files.exists(path)) {
                createDirectory(path);
            }
        });
    }

    public String getConnectionKey() {
        return connectionKey;
    }

    public ClientConnectionConfig getClientConnectionConfig() {
        return clientConnectionConfig;
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }
}
