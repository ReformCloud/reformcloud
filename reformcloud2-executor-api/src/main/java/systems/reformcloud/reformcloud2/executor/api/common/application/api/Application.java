package systems.reformcloud.reformcloud2.executor.api.common.application.api;

import systems.reformcloud.reformcloud2.executor.api.common.application.LoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.application.factory.ApplicationThreadFactory;
import systems.reformcloud.reformcloud2.executor.api.common.application.factory.ApplicationThreadGroup;
import systems.reformcloud.reformcloud2.executor.api.common.application.language.ApplicationLanguage;
import systems.reformcloud.reformcloud2.executor.api.common.application.loader.AppClassLoader;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.language.language.Language;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Application {

    private LoadedApplication application;

    private ExecutorService executorService;

    private AppClassLoader appClassLoader;

    public void init(@Nonnull LoadedApplication application, AppClassLoader loader) {
        this.application = application;
        this.executorService = Executors.newCachedThreadPool(new ApplicationThreadFactory(new ApplicationThreadGroup(application)));
        this.appClassLoader = loader;
    }

    public void onInstallable() {}

    public void onInstalled() {}

    public void onLoad() {}

    public void onEnable() {}

    public void onPreDisable() {}

    public void onDisable() {}

    public void onUninstall() {}

    @Nonnull
    public final File dataFolder() {
        return new File("reformcloud/applications", application.getName());
    }

    @Nullable
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

    public final AppClassLoader getAppClassLoader() {
        return appClassLoader;
    }

    @Nonnull
    public final LoadedApplication getApplication() {
        return application;
    }

    public void log(String log) {
        System.out.println(log);
    }

    @CheckReturnValue
    @Nonnull
    public final ExecutorService getExecutorService() {
        return executorService;
    }
}
