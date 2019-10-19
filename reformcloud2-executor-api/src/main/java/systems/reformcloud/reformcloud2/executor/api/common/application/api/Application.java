package systems.reformcloud.reformcloud2.executor.api.common.application.api;

import systems.reformcloud.reformcloud2.executor.api.common.application.LoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.application.factory.ApplicationThreadFactory;
import systems.reformcloud.reformcloud2.executor.api.common.application.factory.ApplicationThreadGroup;
import systems.reformcloud.reformcloud2.executor.api.common.application.language.ApplicationLanguage;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.language.language.Language;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
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

    public final void registerLanguage(Properties properties) {
        Language language = new ApplicationLanguage(application.getName(), properties);
        LanguageManager.loadAddonMessageFile(application.getName(), language);
    }

    public final void unloadAllLanguageFiles() {
        LanguageManager.unregisterMessageFile(application.getName());
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
