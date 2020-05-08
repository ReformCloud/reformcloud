package systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.mysql;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DefaultDependency;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DependencyLoader;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.repo.DefaultRepositories;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;

import java.net.URL;

public final class MySQLDatabaseDependencyLoader {

    private MySQLDatabaseDependencyLoader() {
        throw new UnsupportedOperationException();
    }

    public static void load(@NotNull DependencyLoader dependencyLoader) {
        URL dependency = dependencyLoader.loadDependency(new DefaultDependency(
                DefaultRepositories.MAVEN_CENTRAL,
                "mysql",
                "mysql-connector-java",
                "8.0.20"
        ));
        Conditions.nonNull(dependency, StringUtil.formatError("dependency load for MySQL database"));
        dependencyLoader.addDependency(dependency);

        dependency = dependencyLoader.loadDependency(new DefaultDependency(
                DefaultRepositories.MAVEN_CENTRAL,
                "org.slf4j",
                "slf4j-api",
                "1.7.25"
        ));
        Conditions.nonNull(dependency, StringUtil.formatError("dependency load for MySQL database"));
        dependencyLoader.addDependency(dependency);

        dependency = dependencyLoader.loadDependency(new DefaultDependency(
                DefaultRepositories.MAVEN_CENTRAL,
                "com.zaxxer",
                "HikariCP",
                "3.4.4"
        ));
        Conditions.nonNull(dependency, StringUtil.formatError("dependency load for MySQL database"));
        dependencyLoader.addDependency(dependency);
    }
}
