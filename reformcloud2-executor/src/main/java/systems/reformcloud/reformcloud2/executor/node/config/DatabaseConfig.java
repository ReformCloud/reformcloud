package systems.reformcloud.reformcloud2.executor.node.config;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;

import java.nio.file.Files;
import java.nio.file.Paths;

public final class DatabaseConfig {

    private JsonConfiguration configuration;

    private DatabaseType type;

    public void load() {
        if (!Files.exists(Paths.get("reformcloud/configs/database.json"))) {
            new JsonConfiguration()
                    .add("type", DatabaseType.FILE)
                    .add("host", "127.0.0.1")
                    .add("port", -1)
                    .add("user", "reformcloud")
                    .add("password", "reformcloud2222")
                    .add("table", "default")
                    .write(Paths.get("reformcloud/configs/database.json"));
        }

        this.configuration = JsonConfiguration.read(Paths.get("reformcloud/configs/database.json"));
        this.type = this.configuration.get("type", DatabaseType.class);
    }

    public void reload() {
        load();
    }

    public void connect(Database database) {
        database.connect(
                configuration.getString("host"),
                configuration.getInteger("port"),
                configuration.getString("user"),
                configuration.getString("password"),
                configuration.getString("table")
        );
    }

    public DatabaseType getType() {
        return type;
    }

    public enum DatabaseType {

        FILE,

        MONGO,

        MYSQL
    }
}
