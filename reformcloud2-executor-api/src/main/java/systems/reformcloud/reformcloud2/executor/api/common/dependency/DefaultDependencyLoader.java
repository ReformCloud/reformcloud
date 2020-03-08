package systems.reformcloud.reformcloud2.executor.api.common.dependency;

import systems.reformcloud.reformcloud2.executor.api.common.dependency.util.DependencyParser;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class DefaultDependencyLoader extends DependencyLoader {

    private static final String path = System.getProperty("reformcloud.lib.path");

    private final List<URL> urls = new ArrayList<>();

    @Override
    public void loadDependencies() {
        DependencyParser.getAllMavenCentralDependencies().forEach(dependency -> {
            System.out.println("Preloading dependency " + dependency.getArtifactID() + " " + dependency.getVersion()
                    + " from repo " + dependency.getRepository().getName() + "...");
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
