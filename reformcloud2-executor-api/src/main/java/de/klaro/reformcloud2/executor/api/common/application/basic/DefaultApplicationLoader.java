package de.klaro.reformcloud2.executor.api.common.application.basic;

import com.google.gson.reflect.TypeToken;
import de.klaro.reformcloud2.executor.api.common.application.*;
import de.klaro.reformcloud2.executor.api.common.application.api.Application;
import de.klaro.reformcloud2.executor.api.common.application.builder.ApplicationConfigBuilder;
import de.klaro.reformcloud2.executor.api.common.application.loader.AppClassLoader;
import de.klaro.reformcloud2.executor.api.common.base.Conditions;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.dependency.DefaultDependencyLoader;
import de.klaro.reformcloud2.executor.api.common.dependency.Dependency;
import de.klaro.reformcloud2.executor.api.common.dependency.DependencyLoader;
import de.klaro.reformcloud2.executor.api.common.language.LanguageManager;
import de.klaro.reformcloud2.executor.api.common.utility.list.Links;
import de.klaro.reformcloud2.executor.api.common.utility.system.DownloadHelper;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
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

        for (File file : Objects.requireNonNull(APP_DIR.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".jar");
            }
        }))) {
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
                    ).create();

                    Conditions.isTrue(applicationConfig.getName() != null, "Application has no name");
                    Conditions.isTrue(applicationConfig.main() != null, "Application must have a main class");
                    Conditions.isTrue(applicationConfig.author() != null, "Application must have a author");
                    Conditions.isTrue(applicationConfig.version() != null, "Application must have a version");

                    load.put(applicationConfig.getName(), applicationConfig);
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }

        applicationHandlers.forEach(new Consumer<ApplicationHandler>() {
            @Override
            public void accept(ApplicationHandler applicationHandler) {
                applicationHandler.onDetectApplications();
            }
        });
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

                URLClassLoader classLoader = new AppClassLoader(new URL[] {
                        config.getValue().applicationFile().toURI().toURL()
                });

                Class<?> main = classLoader.loadClass(config.getValue().main());
                Conditions.isTrue(main != null, "Main-Class of application " + config.getKey() + " not found");
                Application application = (Application) main.getDeclaredConstructor().newInstance();

                System.out.println(LanguageManager.get("successfully-pre-installed-app", config.getKey(), config.getValue().author()));
                application.onInstallable();

                application.init(new DefaultLoadedApplication(this, config.getValue(), main));
                application.onInstalled();
                application.getApplication().setApplicationStatus(ApplicationStatus.INSTALLED);
                System.out.println(LanguageManager.get("successfully-installed-app", config.getKey()));

                applications.add(application);
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        applicationHandlers.forEach(new Consumer<ApplicationHandler>() {
            @Override
            public void accept(ApplicationHandler applicationHandler) {
                applicationHandler.onInstallApplications();
            }
        });
    }

    @Override
    public void loadApplications() {
        applications.forEach(new Consumer<Application>() {
            @Override
            public void accept(Application application) {
                application.onLoad();
                application.getApplication().setApplicationStatus(ApplicationStatus.LOADED);
                System.out.println(LanguageManager.get("successfully-loaded-app", application.getApplication().getName()));
            }
        });

        applicationHandlers.forEach(new Consumer<ApplicationHandler>() {
            @Override
            public void accept(ApplicationHandler applicationHandler) {
                applicationHandler.onLoadApplications();
            }
        });
    }

    @Override
    public void enableApplications() {
        applications.forEach(new Consumer<Application>() {
            @Override
            public void accept(Application application) {
                application.onEnable();
                application.getApplication().setApplicationStatus(ApplicationStatus.ENABLED);
                System.out.println(LanguageManager.get("successfully-enabled-app", application.getApplication().getName()));
            }
        });

        applicationHandlers.forEach(new Consumer<ApplicationHandler>() {
            @Override
            public void accept(ApplicationHandler applicationHandler) {
                applicationHandler.onEnableApplications();
            }
        });
    }

    @Override
    public void disableApplications() {
        applications.forEach(new Consumer<Application>() {
            @Override
            public void accept(Application application) {
                application.getApplication().setApplicationStatus(ApplicationStatus.PRE_DISABLE);
                application.onPreDisable();
                System.out.println(LanguageManager.get("successfully-pre-disabled-app", application.getApplication().getName()));
                application.getApplication().setApplicationStatus(ApplicationStatus.DISABLED);

                application.getApplication().setApplicationStatus(ApplicationStatus.UNINSTALLING);
                application.onUninstall();
                System.out.println(LanguageManager.get("successfully-uninstalled-app", application.getApplication().getName()));
                application.getApplication().setApplicationStatus(ApplicationStatus.UNINSTALLED);
            }
        });
        applications.clear();

        applicationHandlers.forEach(new Consumer<ApplicationHandler>() {
            @Override
            public void accept(ApplicationHandler applicationHandler) {
                applicationHandler.onDisableApplications();
            }
        });
    }

    @Override
    public boolean doSpecificApplicationInstall(InstallableApplication application) {
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
                ).create();

                Conditions.isTrue(applicationConfig.getName() != null, "Application has no name");
                Conditions.isTrue(applicationConfig.main() != null, "Application must have a main class");
                Conditions.isTrue(applicationConfig.author() != null, "Application must have a author");
                Conditions.isTrue(applicationConfig.version() != null, "Application must have a version");

                if (applicationConfig.dependencies().length != 0) {
                    for (Dependency dependency : applicationConfig.dependencies()) {
                        DEPENDENCY_LOADER.addDependency(DEPENDENCY_LOADER.loadDependency(dependency));
                    }
                }

                URLClassLoader classLoader = new AppClassLoader(new URL[] {
                        applicationConfig.applicationFile().toURI().toURL()
                });

                Class<?> main = classLoader.loadClass(applicationConfig.main());
                Conditions.isTrue(main != null, "Main-Class of application " + applicationConfig.getName() + " not found");
                Application app = (Application) main.getDeclaredConstructor().newInstance();

                System.out.println(LanguageManager.get("successfully-pre-installed-app", applicationConfig.getName(), applicationConfig.author()));
                app.onInstallable();

                app.init(new DefaultLoadedApplication(this, applicationConfig, main));
                app.onInstalled();
                app.getApplication().setApplicationStatus(ApplicationStatus.INSTALLED);
                System.out.println(LanguageManager.get("successfully-installed-app", applicationConfig.getName()));

                applications.add(app);
                return true;
            }
        } catch (final Throwable ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean doSpecificApplicationUninstall(LoadedApplication loadedApplication) {
        Application application = Links.filter(applications, new Predicate<Application>() {
            @Override
            public boolean test(Application application) {
                return application.getApplication().getName().equals(loadedApplication.getName());
            }
        });
        if (application == null) {
            return false;
        }

        application.getApplication().setApplicationStatus(ApplicationStatus.PRE_DISABLE);
        application.onPreDisable();
        System.out.println(LanguageManager.get("successfully-pre-disabled-app", application.getApplication().getName()));
        application.getApplication().setApplicationStatus(ApplicationStatus.DISABLED);

        application.getApplication().setApplicationStatus(ApplicationStatus.UNINSTALLING);
        application.onUninstall();
        System.out.println(LanguageManager.get("successfully-uninstalled-app", application.getApplication().getName()));
        application.getApplication().setApplicationStatus(ApplicationStatus.UNINSTALLED);
        return true;
    }

    @Override
    public boolean doSpecificApplicationUninstall(String application) {
        LoadedApplication app = getApplication(application);
        if (app == null) {
            return false;
        }

        return doSpecificApplicationUninstall(app);
    }

    @Override
    public LoadedApplication getApplication(String name) {
        return Links.filterAndApply(applications, new Predicate<Application>() {
            @Override
            public boolean test(Application app) {
                return app.getApplication().getName().equals(name);
            }
        }, new Function<Application, LoadedApplication>() {
            @Override
            public LoadedApplication apply(Application app) {
                return app.getApplication();
            }
        });
    }

    @Override
    public String getApplicationName(LoadedApplication loadedApplication) {
        return loadedApplication.getName();
    }

    @Override
    public List<LoadedApplication> getApplications() {
        return Collections.unmodifiableList(Links.apply(applications, new Function<Application, LoadedApplication>() {
            @Override
            public LoadedApplication apply(Application application) {
                return application.getApplication();
            }
        }));
    }

    @Override
    public void addApplicationHandler(ApplicationHandler applicationHandler) {
        applicationHandlers.add(applicationHandler);
    }
}
