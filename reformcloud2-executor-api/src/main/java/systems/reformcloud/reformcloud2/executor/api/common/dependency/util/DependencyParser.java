package systems.reformcloud.reformcloud2.executor.api.common.dependency.util;

import systems.reformcloud.reformcloud2.executor.api.common.dependency.Dependency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public final class DependencyParser {

    private DependencyParser() {
        throw new UnsupportedOperationException();
    }

    public static Collection<Dependency> getAllMavenCentralDependencies() {
        Collection<Dependency> out = new ArrayList<>();
        for (String dependencyString : getDependenciesFromFile()) {
            String[] split = dependencyString.split(":");
            if (split.length != 3) {
                continue;
            }

            out.add(new MavenCentralDependency(split[0], split[1], split[2]));
        }

        return out;
    }

    private static Collection<String> getDependenciesFromFile() {
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
