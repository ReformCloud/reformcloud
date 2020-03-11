package systems.reformcloud.reformcloud2.backends;

import systems.reformcloud.reformcloud2.backends.ftp.FTPTemplateBackend;
import systems.reformcloud.reformcloud2.backends.sftp.SFTPTemplateBackend;
import systems.reformcloud.reformcloud2.backends.url.URLTemplateBackend;
import systems.reformcloud.reformcloud2.executor.api.common.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DefaultDependencyLoader;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DependencyLoader;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.util.DependencyParser;

import java.net.URL;
import java.util.HashMap;

public class TemplateBackendApplication extends Application {

    public static final DependencyLoader LOADER = new DefaultDependencyLoader();

    @Override
    public void onLoad() {
        DependencyParser.getAllDependencies("dependencies.txt", new HashMap<>()).forEach(e -> {
            URL dependencyURL = TemplateBackendApplication.LOADER.loadDependency(e);
            Conditions.nonNull(dependencyURL, "Dependency load for " + e.getArtifactID() + " failed");
            TemplateBackendApplication.LOADER.addDependency(dependencyURL);
        });

        FTPTemplateBackend.load(dataFolder().getPath());
        SFTPTemplateBackend.load(dataFolder().getPath());
        URLTemplateBackend.load(dataFolder().getPath());
    }

    @Override
    public void onDisable() {
        URLTemplateBackend.unload();
        FTPTemplateBackend.unload();
        SFTPTemplateBackend.unload();
    }
}
