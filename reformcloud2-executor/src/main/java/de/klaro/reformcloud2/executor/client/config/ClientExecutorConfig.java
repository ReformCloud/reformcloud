package de.klaro.reformcloud2.executor.client.config;

import com.google.gson.reflect.TypeToken;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.logger.setup.Setup;
import de.klaro.reformcloud2.executor.api.common.logger.setup.basic.DefaultSetup;
import de.klaro.reformcloud2.executor.api.common.logger.setup.basic.DefaultSetupQuestion;
import de.klaro.reformcloud2.executor.client.ClientExecutor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static de.klaro.reformcloud2.executor.api.common.utility.list.Links.newCollection;
import static de.klaro.reformcloud2.executor.api.common.utility.system.SystemHelper.createDirectory;

public final class ClientExecutorConfig {

    private final Setup setup = new DefaultSetup();

    private static final Collection<Path> PATHS = newCollection(
            new Function<String, Path>() {
                @Override
                public Path apply(String s) {
                    return Paths.get(s);
                }
            },
            "reformcloud/temp",
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
                new Predicate<String>() {
                    @Override
                    public boolean test(String s) {
                        return true;
                    }
                },
                new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        new JsonConfiguration().add("key", s).write("reformcloud/files/.connection/connection.json");
                    }
                })
        ).addQuestion(new DefaultSetupQuestion("Please write the start host", "Please write an ip address",
                new Predicate<String>() {
                    @Override
                    public boolean test(String s) {
                        return s.split("\\.").length == 4;
                    }
                },
                new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        startHost.set(s);
                    }
                })
        ).addQuestion(new DefaultSetupQuestion("Please enter the max memory of the client", "Please write a number bigger than 128",
                new Predicate<String>() {
                    @Override
                    public boolean test(String s) {
                        try {
                            int i = Integer.parseInt(s);
                            return i > 128;
                        } catch (final Throwable throwable) {
                            return false;
                        }
                    }
                },
                new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        new JsonConfiguration()
                                .add("config", new ClientConfig(Integer.parseInt(s), -1, 99.0, startHost.get()))
                                .write(ClientConfig.PATH);
                    }
                })
        ).addQuestion(new DefaultSetupQuestion("Please write the ip address of the controller", "Please write the real ip ;)",
                new Predicate<String>() {
                    @Override
                    public boolean test(String s) {
                        return s.split("\\.").length == 4;
                    }
                },
                new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        controllerHost.set(s);
                    }
                })
        ).addQuestion(new DefaultSetupQuestion("Please write the controller network port (default: 2008)", "The port must be bigger than 0",
                new Predicate<String>() {
                    @Override
                    public boolean test(String s) {
                        try {
                            int i = Integer.parseInt(s);
                            return i > 0;
                        } catch (final Throwable throwable) {
                            return false;
                        }
                    }
                },
                new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        new JsonConfiguration()
                                .add("config", new ClientConnectionConfig(controllerHost.get(), Integer.parseInt(s)))
                                .write(ClientConnectionConfig.PATH);
                    }
                })
        ).startSetup(ClientExecutor.getInstance().getLoggerBase());
    }

    private void createDirectories() {
        PATHS.forEach(new Consumer<Path>() {
            @Override
            public void accept(Path path) {
                if (!Files.exists(path)) {
                    createDirectory(path);
                }
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
