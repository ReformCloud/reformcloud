package systems.reformcloud.reformcloud2.executor.api.common.dependency.util;

import systems.reformcloud.reformcloud2.executor.api.common.dependency.DefaultDependency;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.Dependency;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.repo.DefaultRepositories;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.repo.Repository;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public final class DependencyParser {

    private DependencyParser() {
        throw new UnsupportedOperationException();
    }

    public static Collection<Dependency> getAllDependencies(@Nonnull String internalFilePath, @Nonnull Map<String, Repository> repositoryGetter) {
        Collection<Dependency> out = new ArrayList<>();
        for (String dependencyString : getDependenciesFromFile(internalFilePath)) {
            String[] split = dependencyString.split(":");
            if (split.length != 3) {
                continue;
            }

            out.add(new DefaultDependency(
                    repositoryGetter.getOrDefault(split[0] + ":" + split[1], DefaultRepositories.MAVEN_CENTRAL),
                    split[0], split[1], split[2])
            );
        }

        return out;
    }

    private static Collection<String> getDependenciesFromFile(String internalFilePath) {
        Collection<String> out = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(DependencyParser.class.getClassLoader().getResourceAsStream("internal/dependencies.txt"))))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                out.add(line);
            }
        } catch (final IOException ex) {
            throw new RuntimeException("Unable to load internal dependencies file", ex);
        }

        return out;
    }
}
