package systems.reformcloud.reformcloud2.executor.api.common.dependency;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.util.DependencyParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class DefaultDependencyLoader extends DependencyLoader {

    private static final String LOADING = "Preloading dependency %s %s from repo %s...";

    private static final String PATH = System.getProperty("reformcloud.lib.path");

    private final List<URL> urls = new ArrayList<>();

    @Override
    public void loadDependencies() {
        DependencyParser.getAllDependencies("internal/dependencies.txt", new HashMap<>()).forEach(e -> {
            System.out.println(String.format(LOADING, e.getArtifactID(), e.getVersion(), e.getRepository().getName()));
            urls.add(loadDependency(e));
        });
    }

    @Override
    public void addDependencies() {
        urls.forEach(this::addDependency);
    }

    @Override
    public URL loadDependency(@NotNull Dependency dependency) {
        Path path;
        if (DefaultDependencyLoader.PATH != null) {
            path = Paths.get(DefaultDependencyLoader.PATH + "/" + dependency.getPath());
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
    public void addDependency(@NotNull URL depend) {
        DefaultDependencyLoader.this.addURL(depend);
    }
}
