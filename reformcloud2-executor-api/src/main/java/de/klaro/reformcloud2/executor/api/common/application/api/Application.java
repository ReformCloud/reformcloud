package de.klaro.reformcloud2.executor.api.common.application.api;

import de.klaro.reformcloud2.executor.api.common.application.LoadedApplication;
import de.klaro.reformcloud2.executor.api.common.application.factory.ApplicationThreadFactory;
import de.klaro.reformcloud2.executor.api.common.application.factory.ApplicationThreadGroup;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Application {

    private LoadedApplication application;

    private ExecutorService executorService;

    public void init(LoadedApplication application) {
        this.application = application;
        this.executorService = Executors.newCachedThreadPool(new ApplicationThreadFactory(new ApplicationThreadGroup(application)));
    }

    public void onInstallable() {}

    public void onInstalled() {}

    public void onLoad() {}

    public void onEnable() {}

    public void onPreDisable() {}

    public void onDisable() {}

    public void onUninstall() {}

    public final File dataFolder() {
        return new File("reformcloud/applications", application.getName());
    }

    public final InputStream getResourceAsStream(String name) {
        return getClass().getClassLoader().getResourceAsStream(name);
    }

    public final LoadedApplication getApplication() {
        return application;
    }

    public void log(String log) {
        System.out.println(log);
    }

    public final ExecutorService getExecutorService() {
        return executorService;
    }
}
