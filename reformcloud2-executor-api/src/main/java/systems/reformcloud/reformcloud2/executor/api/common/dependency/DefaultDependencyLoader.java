package systems.reformcloud.reformcloud2.executor.api.common.dependency;

import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.repo.DefaultRepositories;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public final class DefaultDependencyLoader extends DependencyLoader {

    private static final String path = System.getProperty("reformcloud.lib.path");

    static {
        Properties properties = new Properties();
        try (InputStream inputStream = DefaultDependencyLoader.class.getClassLoader().getResourceAsStream("internal/versions.properties")) {
            Conditions.nonNull(inputStream, "The current build you are running on is broken, try another");
            properties.load(inputStream);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        PROPERTIES = properties;
    }

    private static final Properties PROPERTIES;

    private static final List<Dependency> DEFAULT_DEPENDENCIES = Arrays.asList(
            new DefaultDependency(
                    DefaultRepositories.MAVEN_CENTRAL,
                    "io.netty", "netty-all", PROPERTIES
            ), new DefaultDependency(
                    DefaultRepositories.MAVEN_CENTRAL,
                    "org.jline", "jline", PROPERTIES
            ), new DefaultDependency(
                    DefaultRepositories.MAVEN_CENTRAL,
                    "com.google.code.gson", "gson", PROPERTIES
            ), new DefaultDependency(
                    DefaultRepositories.MAVEN_CENTRAL,
                    "org.reflections", "reflections", PROPERTIES
            ), new DefaultDependency(
                    DefaultRepositories.MAVEN_CENTRAL,
                    "com.google.guava", "guava", PROPERTIES
            ), new DefaultDependency(
                    DefaultRepositories.MAVEN_CENTRAL,
                    "org.javassist", "javassist", PROPERTIES
            ), new DefaultDependency(
                    DefaultRepositories.MAVEN_CENTRAL,
                    "org.fusesource.jansi", "jansi", PROPERTIES
            )
    );

    private final List<URL> urls = new ArrayList<>();

    @Override
    public void loadDependencies() {
        DEFAULT_DEPENDENCIES.forEach(dependency -> {
            System.out.println("Preloading dependency " + dependency.getArtifactID() + " from repo " + dependency.getRepository().getName() + "...");
            urls.add(loadDependency(dependency));
        });
    }

    @Override
    public void addDependencies() {
        urls.forEach(this::addDependency);
    }

    @Override
    public URL loadDependency(@Nonnull Dependency dependency) {
        Path path;
        if (DefaultDependencyLoader.path != null) {
            path = Paths.get(DefaultDependencyLoader.path + "/" + dependency.getPath());
        } else {
            path = dependency.getPath();
        }

        try {
            dependency.prepareIfUpdate();
            if (!Files.exists(path)) {
                dependency.download();
            }

            return path.toUri().toURL();
        } catch (final MalformedURLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public void addDependency(@Nonnull URL depend) {
        DefaultDependencyLoader.this.addURL(depend);
    }
}
