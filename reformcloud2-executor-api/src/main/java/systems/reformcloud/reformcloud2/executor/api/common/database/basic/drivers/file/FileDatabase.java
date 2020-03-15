package systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.file;

import de.derklaro.projects.deer.api.provider.DatabaseProvider;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DefaultDependency;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.repo.DefaultRepositories;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.maps.AbsentMap;

import javax.annotation.Nonnull;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public final class FileDatabase extends Database<Path> {

    private String table;

    private final Map<String, DatabaseReader> perTableReader = new AbsentMap<>();

    public FileDatabase() {
        this.initDependencies();

        try {
            Class.forName("de.derklaro.projects.deer.executor.BasicDatabaseDriver");
        } catch (final ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void connect(@Nonnull String host, int port, @Nonnull String userName, @Nonnull String password, @Nonnull String table) {
        this.table = "reformcloud/.database/" + table;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void reconnect() {
    }

    @Override
    public void disconnect() {
    }

    @Override
    public boolean createDatabase(String name) {
        DatabaseProvider.getDatabaseDriver().getDatabase(new File(this.table, name), file -> null, 1);
        return true;
    }

    @Override
    public boolean deleteDatabase(String name) {
        DatabaseProvider.getDatabaseDriver().deleteDatabase(new File(this.table, name));
        perTableReader.remove(name);
        return true;
    }

    @Override
    public DatabaseReader createForTable(String table) {
        return perTableReader.putIfAbsent(table, new FileDatabaseReader(this.table, table));
    }

    @Nonnull
    @Override
    public Path get() {
        return Paths.get(table);
    }

    private void initDependencies() {
        URL url = DEPENDENCY_LOADER.loadDependency(new DefaultDependency(
                DefaultRepositories.REFORMCLOUD,
                "de.derklaro.projects.deer",
                "project-deer-executor",
                "1.0-SNAPSHOT"
        ));
        Conditions.nonNull(url, StringUtil.formatError("dependency executor load for file database"));
        DEPENDENCY_LOADER.addDependency(url);

        URL apiUrl = DEPENDENCY_LOADER.loadDependency(new DefaultDependency(
                DefaultRepositories.REFORMCLOUD,
                "de.derklaro.projects.deer",
                "project-deer-api",
                "1.0-SNAPSHOT"
        ));
        Conditions.nonNull(apiUrl, StringUtil.formatError("dependency api load for file database"));
        DEPENDENCY_LOADER.addDependency(apiUrl);
    }
}
