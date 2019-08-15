package de.klaro.reformcloud2.executor.api.common.dependency;

import de.klaro.reformcloud2.executor.api.common.dependency.repo.DefaultRepositories;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

final class DefaultDependencyLoader extends DependencyLoader {

    static {
        Properties properties = new Properties();
        try (InputStream inputStream = DefaultDependencyLoader.class.getClassLoader().getResourceAsStream("internal/versions.properties")) {
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
                    "jline", "jline", PROPERTIES
            ), new DefaultDependency(
                    DefaultRepositories.MAVEN_CENTRAL,
                    "com.google.code.gson", "gson", PROPERTIES
            ), new DefaultDependency(
                    DefaultRepositories.MAVEN_CENTRAL,
                    "org.yaml", "snakeyaml", PROPERTIES
            )
    );

    private final List<URL> urls = new ArrayList<>();

    @Override
    public void loadDependencies() {
        DEFAULT_DEPENDENCIES.forEach(new Consumer<Dependency>() {
            @Override
            public void accept(Dependency dependency) {
                System.out.println("Preloading dependency " + dependency.getArtifactID() + " from repo " + dependency.getRepository().getName() + "...");
                try {
                    dependency.prepareIfUpdate();
                    if (Files.exists(dependency.getPath())) {
                        urls.add(dependency.getPath().toUri().toURL());
                    } else {
                        dependency.download();
                        urls.add(dependency.getPath().toUri().toURL());
                    }
                } catch (final MalformedURLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public void addDependencies() {
        urls.forEach(new Consumer<URL>() {
            @Override
            public void accept(URL url) {
                DefaultDependencyLoader.this.addURL(url);
            }
        });
    }
}
