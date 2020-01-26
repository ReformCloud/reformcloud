package systems.reformcloud.reformcloud2.backends;

import systems.reformcloud.reformcloud2.backends.ftp.FTPTemplateBackend;
import systems.reformcloud.reformcloud2.backends.sftp.SFTPTemplateBackend;
import systems.reformcloud.reformcloud2.backends.url.URLTemplateBackend;
import systems.reformcloud.reformcloud2.executor.api.common.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DefaultDependency;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DefaultDependencyLoader;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DependencyLoader;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.repo.DefaultRepositories;

import java.net.URL;
import java.util.Properties;

public class TemplateBackendApplication extends Application {

    public static final DependencyLoader LOADER = new DefaultDependencyLoader();

    @Override
    public void onEnable() {
        Properties properties = new Properties();
        properties.setProperty("commons-net", "3.6");

        URL url = TemplateBackendApplication.LOADER.loadDependency(new DefaultDependency(
                DefaultRepositories.MAVEN_CENTRAL,
                "commons-net",
                "commons-net",
                properties
        ));
        if (url != null) {
            TemplateBackendApplication.LOADER.addDependency(url);
            FTPTemplateBackend.load(dataFolder().getPath());
        }

        properties.setProperty("jsch", "0.1.55");

        url = TemplateBackendApplication.LOADER.loadDependency(new DefaultDependency(
                DefaultRepositories.MAVEN_CENTRAL,
                "com.jcraft",
                "jsch",
                properties
        ));
        if (url != null) {
            TemplateBackendApplication.LOADER.addDependency(url);
            SFTPTemplateBackend.load(dataFolder().getPath());
        }

        URLTemplateBackend.load(dataFolder().getPath());
    }

    @Override
    public void onDisable() {
        URLTemplateBackend.unload();
        FTPTemplateBackend.unload();
        SFTPTemplateBackend.unload();
    }
}
