package systems.reformcloud.reformcloud2.executor.api.common.application.basic;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.application.*;
import systems.reformcloud.reformcloud2.executor.api.common.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.common.application.builder.ApplicationConfigBuilder;
import systems.reformcloud.reformcloud2.executor.api.common.application.loader.AppClassLoader;
import systems.reformcloud.reformcloud2.executor.api.common.application.unsafe.ApplicationUnsafe;
import systems.reformcloud.reformcloud2.executor.api.common.application.updater.ApplicationUpdateRepository;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DefaultDependencyLoader;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.Dependency;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DependencyLoader;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.DownloadHelper;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class DefaultApplicationLoader implements ApplicationLoader {

    private static final File APP_DIR = new File("reformcloud/applications");

    private final Map<String, ApplicationConfig> load = new HashMap<>();

    private final List<Application> applications = new ArrayList<>();

    private static final DependencyLoader DEPENDENCY_LOADER = new DefaultDependencyLoader();

    private final List<ApplicationHandler> applicationHandlers = new ArrayList<>();

    @Override
    public void detectApplications() {
        Conditions.isTrue(APP_DIR.isDirectory());

        for (File file : Objects.requireNonNull(APP_DIR.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".jar")))) {
            try (JarFile jarFile = new JarFile(file)) {
                JarEntry appConfig = jarFile.getJarEntry("application.json");
                if (appConfig == null) {
                    appConfig = jarFile.getJarEntry("app.json");
                }

                Conditions.isTrue(appConfig != null, "Application has to contain a 'application.json' or 'app.json'");
                try (InputStream inputStream = jarFile.getInputStream(appConfig)) {
                    JsonConfiguration configurable = new JsonConfiguration(inputStream);
                    ApplicationConfig applicationConfig = new ApplicationConfigBuilder(
                            configurable.getString("name"),
                            configurable.getString("main"),
                            configurable.getString("author"),
                            configurable.getString("version"),
                            file,
                            appConfig
                    ).withDependencies(
                            configurable.getOrDefault("dependencies", new TypeToken<List<Dependency>>() {}.getType(), new ArrayList<>())
                    ).withDescription(
                            configurable.getOrDefault("description", (String) null)
                    ).withWebsite(
                            configurable.getOrDefault("website", (String) null)
                    ).withImplementedVersion(
                            configurable.getOrDefault("impl-version", (String) null)
                    ).create();

                    ApplicationUnsafe.checkIfUnsafe(applicationConfig);

                    load.put(applicationConfig.getName(), applicationConfig);
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }

        applicationHandlers.forEach(ApplicationHandler::onDetectApplications);
    }

    @Override
    public void installApplications() {
        for (Map.Entry<String, ApplicationConfig> config : load.entrySet()) {
            try {
                if (config.getValue().dependencies().length != 0) {
                    for (Dependency dependency : config.getValue().dependencies()) {
                        DEPENDENCY_LOADER.addDependency(DEPENDENCY_LOADER.loadDependency(dependency));
                    }
                }

                AppClassLoader classLoader = new AppClassLoader(new URL[] {
                        config.getValue().applicationFile().toURI().toURL()
                }, Thread.currentThread().getContextClassLoader());

                Class<?> main = classLoader.loadClass(config.getValue().main());
                Conditions.isTrue(main != null, "Main-Class of application " + config.getKey() + " not found");
                Application application = (Application) main.getDeclaredConstructor().newInstance();

                System.out.println(LanguageManager.get("successfully-pre-installed-app", config.getKey(), config.getValue().author()));
                application.onInstallable();

                application.init(new DefaultLoadedApplication(this, config.getValue(), main), classLoader);
                application.onInstalled();
                application.getApplication().setApplicationStatus(ApplicationStatus.INSTALLED);
                System.out.println(LanguageManager.get("successfully-installed-app", config.getKey()));

                if (application.getUpdateRepository() != null) {
                    application.getUpdateRepository().fetchOrigin();
                }

                applications.add(application);
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        applicationHandlers.forEach(ApplicationHandler::onInstallApplications);
    }

    @Override
    public void loadApplications() {
        applications.forEach(application -> {
            application.onLoad();
            application.getApplication().setApplicationStatus(ApplicationStatus.LOADED);
            System.out.println(LanguageManager.get("successfully-loaded-app", application.getApplication().getName()));
        });

        applicationHandlers.forEach(ApplicationHandler::onLoadApplications);
    }

    @Override
    public void enableApplications() {
        applications.forEach(application -> {
            application.onEnable();
            application.getApplication().setApplicationStatus(ApplicationStatus.ENABLED);
            System.out.println(LanguageManager.get("successfully-enabled-app", application.getApplication().getName()));
        });

        applicationHandlers.forEach(ApplicationHandler::onEnableApplications);
    }

    @Override
    public void disableApplications() {
        applications.forEach(application -> {
            application.getApplication().setApplicationStatus(ApplicationStatus.PRE_DISABLE);
            application.onPreDisable();
            System.out.println(LanguageManager.get("successfully-pre-disabled-app", application.getApplication().getName()));
            application.getApplication().setApplicationStatus(ApplicationStatus.DISABLED);

            application.onDisable();

            application.getApplication().setApplicationStatus(ApplicationStatus.UNINSTALLING);
            application.onUninstall();
            System.out.println(LanguageManager.get("successfully-uninstalled-app", application.getApplication().getName()));
            application.getApplication().setApplicationStatus(ApplicationStatus.UNINSTALLED);

            this.handleUpdate(application);

            application.unloadAllLanguageFiles();
            application.getAppClassLoader().close();
        });
        applications.clear();

        applicationHandlers.forEach(ApplicationHandler::onDisableApplications);
    }

    @Override
    public void fetchAllUpdates() {
        this.applications.forEach(this::handleUpdate);
    }

    @Override
    public void fetchUpdates(@Nonnull String application) {
        Streams.filterToReference(this.applications,
                e -> e.getApplication().getName().equalsIgnoreCase(application)).ifPresent(this::handleUpdate);
    }

    @Override
    public boolean doSpecificApplicationInstall(@Nonnull InstallableApplication application) {
        DownloadHelper.downloadAndDisconnect(application.url(), "reformcloud/applications/" + application.getName() + ".jar");
        File file = new File("reformcloud/applications/" + application.getName() + ".jar");
        if (!file.exists()) {
            return false;
        }

        try (JarFile jarFile = new JarFile(file)) {
            JarEntry appConfig = jarFile.getJarEntry("application.json");
            if (appConfig == null) {
                appConfig = jarFile.getJarEntry("app.json");
            }

            Conditions.isTrue(appConfig != null, "Application has to contain a 'application.json' or 'app.json'");
            try (InputStream inputStream = jarFile.getInputStream(appConfig)) {
                JsonConfiguration configurable = new JsonConfiguration(inputStream);
                ApplicationConfig applicationConfig = new ApplicationConfigBuilder(
                        configurable.getString("name"),
                        configurable.getString("main"),
                        configurable.getString("author"),
                        configurable.getString("version"),
                        file,
                        appConfig
                ).withDependencies(
                        configurable.get("dependencies", new TypeToken<List<Dependency>>() {})
                ).withDescription(
                        configurable.getString("description")
                ).withWebsite(
                        configurable.getString("website")
                ).withImplementedVersion(
                        configurable.getString("impl-version")
                ).create();

                ApplicationUnsafe.checkIfUnsafe(applicationConfig);

                if (applicationConfig.dependencies().length != 0) {
                    for (Dependency dependency : applicationConfig.dependencies()) {
                        DEPENDENCY_LOADER.addDependency(DEPENDENCY_LOADER.loadDependency(dependency));
                    }
                }

                AppClassLoader classLoader = new AppClassLoader(new URL[] {
                        applicationConfig.applicationFile().toURI().toURL()
                }, Thread.currentThread().getContextClassLoader());

                Class<?> main = classLoader.loadClass(applicationConfig.main());
                Conditions.isTrue(main != null, "Main-Class of application " + applicationConfig.getName() + " not found");
                Application app = (Application) main.getDeclaredConstructor().newInstance();

                System.out.println(LanguageManager.get("successfully-pre-installed-app", applicationConfig.getName(), applicationConfig.author()));
                app.onInstallable();

                app.init(new DefaultLoadedApplication(this, applicationConfig, main), classLoader);
                app.onInstalled();
                app.getApplication().setApplicationStatus(ApplicationStatus.INSTALLED);
                System.out.println(LanguageManager.get("successfully-installed-app", applicationConfig.getName()));

                if (app.getUpdateRepository() != null) {
                    app.getUpdateRepository().fetchOrigin();
                }

                applications.add(app);
                return true;
            }
        } catch (final Throwable ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean doSpecificApplicationUninstall(@Nonnull LoadedApplication loadedApplication) {
        Application application = Streams.filter(applications, application1 -> application1.getApplication().getName().equals(loadedApplication.getName()));
        if (application == null) {
            return false;
        }

        application.getApplication().setApplicationStatus(ApplicationStatus.PRE_DISABLE);
        application.onPreDisable();
        System.out.println(LanguageManager.get("successfully-pre-disabled-app", application.getApplication().getName()));
        application.getApplication().setApplicationStatus(ApplicationStatus.DISABLED);

        application.onDisable();

        application.getApplication().setApplicationStatus(ApplicationStatus.UNINSTALLING);
        application.onUninstall();
        System.out.println(LanguageManager.get("successfully-uninstalled-app", application.getApplication().getName()));
        application.getApplication().setApplicationStatus(ApplicationStatus.UNINSTALLED);

        this.handleUpdate(application);

        application.unloadAllLanguageFiles();
        application.getAppClassLoader().close();
        return true;
    }

    @Override
    public boolean doSpecificApplicationUninstall(@Nonnull String application) {
        LoadedApplication app = getApplication(application);
        if (app == null) {
            return false;
        }

        return doSpecificApplicationUninstall(app);
    }

    @Override
    public LoadedApplication getApplication(@Nonnull String name) {
        return Streams.filterAndApply(applications, app -> app.getApplication().getName().equals(name), Application::getApplication);
    }

    @Nonnull
    @Override
    public String getApplicationName(@Nonnull LoadedApplication loadedApplication) {
        return loadedApplication.getName();
    }

    @Nonnull
    @Override
    public List<LoadedApplication> getApplications() {
        return Collections.unmodifiableList(Streams.apply(applications, Application::getApplication));
    }

    @Override
    public void addApplicationHandler(@Nonnull ApplicationHandler applicationHandler) {
        applicationHandlers.add(applicationHandler);
    }

    private void handleUpdate(Application application) {
        // Do not fetch updates on development builds
        if (System.getProperty("reformcloud.runner.specification").equals("SNAPSHOT")) {
            return;
        }

        ApplicationUpdateRepository repository = application.getUpdateRepository();
        if (repository == null || !repository.isNewVersionAvailable() || repository.getUpdate() == null) {
            return;
        }

        SystemHelper.createDirectory(Paths.get("reformcloud/.update/apps"));

        System.out.println(LanguageManager.get(
                "application-download-update",
                application.getApplication().applicationConfig().getName(),
                application.getApplication().applicationConfig().version(),
                repository.getUpdate().getNewVersion(),
                repository.getName(),
                repository.getUpdate().getDownloadUrl()
        ));
        DownloadHelper.downloadAndDisconnect(
                repository.getUpdate().getDownloadUrl(),
                "reformcloud/.update/apps/" + application.getApplication().getName()
                        + "-" + repository.getUpdate().getNewVersion() + ".jar"
        );
    }
}
