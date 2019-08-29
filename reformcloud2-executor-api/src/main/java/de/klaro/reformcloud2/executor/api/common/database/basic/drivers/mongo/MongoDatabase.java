package de.klaro.reformcloud2.executor.api.common.database.basic.drivers.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.klaro.reformcloud2.executor.api.common.database.Database;
import de.klaro.reformcloud2.executor.api.common.dependency.DefaultDependency;
import de.klaro.reformcloud2.executor.api.common.dependency.repo.DefaultRepositories;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Properties;

public final class MongoDatabase extends Database<com.mongodb.client.MongoDatabase> {

    public MongoDatabase() {
        Properties properties = new Properties();
        properties.setProperty("mongo-java-driver", "3.11.0");

        DEPENDENCY_LOADER.loadDependency(new DefaultDependency(
                DefaultRepositories.MAVEN_CENTRAL,
                "org.mongodb",
                "mongo-java-driver",
                properties
        ));
    }

    private MongoClient mongoClient;

    private com.mongodb.client.MongoDatabase mongoDatabase;

    private String host;

    private int port;

    private String userName;

    private String password;

    private String table;

    @Override
    public void connect(String host, int port, String userName, String password, String table) {
        if (!isConnected()) {
            this.host = host;
            this.port = port;
            this.userName = userName;
            this.password = password;
            this.table = table;

            try {
                this.mongoClient = MongoClients.create(
                        MessageFormat.format(
                                "mongodb://{0}:{1}@{2}:{3}/{4}",
                                userName,
                                URLEncoder.encode(password, StandardCharsets.UTF_8.name()),
                                host,
                                Integer.toString(port),
                                table
                        )
                );
                this.mongoDatabase = mongoClient.getDatabase(table);
            } catch (final UnsupportedEncodingException ex) {
                ex.printStackTrace(); //Should never happen
            }
        }
    }

    @Override
    public boolean isConnected() {
        return mongoClient != null;
    }

    @Override
    public void reconnect() {
        disconnect();
        connect(host, port, userName, password, table);
    }

    @Override
    public void disconnect() {
        if (isConnected()) {
            this.mongoClient.close();
            this.mongoClient = null;
        }
    }

    @Override
    public com.mongodb.client.MongoDatabase get() {
        return mongoDatabase;
    }
}
